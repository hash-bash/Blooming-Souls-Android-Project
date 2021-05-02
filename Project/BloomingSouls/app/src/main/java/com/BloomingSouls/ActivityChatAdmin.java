package com.BloomingSouls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.github.library.bubbleview.BubbleTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import dmax.dialog.SpotsDialog;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ActivityChatAdmin extends BaseBottomNavigationActivityAdmin implements OfflineConnectivityReceiver.ConnectivityReceiverListener {
    ConstraintLayout activity;
    EmojiconEditText emojiconEditText;
    ImageView emojiButton, submitButton;
    EmojIconActions emojIconActions;
    NightModeSharedPref sharedpref;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    IntentFilter intentFilter;
    OfflineConnectivityReceiver offlineConnectivityReceiver;

    boolean doubleBackToExitPressedOnce = false;

    private FirebaseListAdapter<ClassChatMessage> adapter;

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        sharedpref = new NightModeSharedPref(this);
        if (sharedpref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        activity = findViewById(R.id.c_layout);
        emojiButton = findViewById(R.id.emoji_button);
        submitButton = findViewById(R.id.submit_button);
        emojiconEditText = findViewById(R.id.emojicon_edit_text);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        offlineConnectivityReceiver = new OfflineConnectivityReceiver();

        emojIconActions = new EmojIconActions(getApplicationContext(), activity, emojiButton, emojiconEditText);
        emojIconActions.ShowEmojicon();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Chat Messages");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.emojicon_edit_text);
                if (!(input.getText().toString()).isEmpty()) {
                    {
                        FirebaseDatabase.getInstance().getReference("Chat Messages").push().setValue(new ClassChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                        emojiconEditText.getText().clear();
                    }
                }
            }
        });

        displayChatMessage();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        registerReceiver(offlineConnectivityReceiver, intentFilter);
        BSApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        unregisterReceiver(offlineConnectivityReceiver);
    }

    @Override
    int getLayoutId1() {
        return R.layout.chat_activity;
    }

    @Override
    int getBottomNavigationMenuItemId1() {
        return R.id.navigation_talk;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            changeActivity();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteCache(this);
    }

    private void changeActivity() {
        String activityName = "ActivityChatAdmin.class";
        Intent intent = new Intent(ActivityChatAdmin.this, OfflineAlertIntent.class);
        intent.putExtra("key", activityName);
        startActivity(intent);
    }

    private void displayChatMessage() {
        ListView listOfMessage = findViewById(R.id.list_of_message);

        FirebaseListOptions<ClassChatMessage> options = new FirebaseListOptions.Builder<ClassChatMessage>()
                .setQuery(mRef, ClassChatMessage.class)
                .setLayout(R.layout.chat_row_item)
                .build();

        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).build();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_white_card);

        adapter = new FirebaseListAdapter<ClassChatMessage>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull ClassChatMessage model, int position) {
                TextView messageUser, messageTime;
                BubbleTextView messageText;
                messageText = v.findViewById(R.id.message_text);
                messageTime = v.findViewById(R.id.message_time);
                messageUser = v.findViewById(R.id.message_user);

                messageUser.setText(model.getMessageUser());
                messageText.setText(model.getMessageText());
                messageTime.setText(DateFormat.format("dd/MM (HH:MM)", model.getMessageTime()));

                dialog.dismiss();
            }
        };

        listOfMessage.setAdapter(adapter);
    }
}
