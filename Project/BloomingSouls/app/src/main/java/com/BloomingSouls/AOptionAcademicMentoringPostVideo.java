package com.BloomingSouls;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;

public class AOptionAcademicMentoringPostVideo extends AppCompatActivity {
    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;

    RecyclerView recyclerView;
    FirebaseDatabase database;
    Toolbar toolbar;
    String name;
    Dialog popAddPost;
    ImageView popupAddBtn, blank, img1, img2;
    ProgressBar popupClickProgress;
    ClassYoutube classVideo;
    EditText videoName, youtubeId;
    YouTubePlayerView videoView;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Button searchBtn, chooseBtn;
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<ClassYoutube, YoutubeAdapter> firebaseRecyclerAdapter;
    Uri imageUriResultCrop;
    Uri pickedImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_post_video);

        recyclerView = findViewById(R.id.recyclerview_ShowVideo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.search_menu);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        classVideo = new ClassYoutube();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("AcademicMentoringVideos");

        iniPopup();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });

        FirebaseRecyclerOptions<ClassYoutube> options =
                new FirebaseRecyclerOptions.Builder<ClassYoutube>()
                        .setQuery(databaseReference, ClassYoutube.class)
                        .build();

        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).build();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_white_card);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ClassYoutube, YoutubeAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull YoutubeAdapter holder, int position, @NonNull ClassYoutube model) {
                holder.setYoutube(model.getVideoId(), model.getTitle(), model.getImageUrl());
                holder.setOnClicklistener(new YoutubeAdapter.Clicklistener() {
                    @Override
                    public void onItemLongClick(View view, int position) {
                        name = getItem(position).getTitle();
                        showDeleteDialog(name);
                    }
                });
                dialog.dismiss();
            }

            @NonNull
            @Override
            public YoutubeAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.youtube_row_item, parent, false);
                return new YoutubeAdapter(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            pickedImgUri = data.getData();
            if (pickedImgUri != null) {
                startCrop(pickedImgUri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                imageUriResultCrop = UCrop.getOutput(data);
            } else {
                Intent intent = new Intent(getApplicationContext(), AOptionLifeCoachingPostVideo.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_firebase);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void iniPopup() {
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_video_add_new);

        videoView = popAddPost.findViewById(R.id.youtube_player_view);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);
        videoName = popAddPost.findViewById(R.id.popup_title);
        youtubeId = popAddPost.findViewById(R.id.popup_yt);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        searchBtn = popAddPost.findViewById(R.id.search);
        chooseBtn = popAddPost.findViewById(R.id.thumbnail);
        blank = popAddPost.findViewById(R.id.blank);
        img1 = popAddPost.findViewById(R.id.img1);
        img2 = popAddPost.findViewById(R.id.img2);

        img1.setOnClickListener(null);
        img1.setOnClickListener(null);

        popupAddBtn.setVisibility(View.VISIBLE);
        blank.setVisibility(View.VISIBLE);
        popupClickProgress.setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.INVISIBLE);

        getLifecycle().addObserver(videoView);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(youtubeId.getText().toString().isEmpty())) {
                    videoView.setVisibility(View.VISIBLE);
                    blank.setVisibility(View.INVISIBLE);

                    String videoId = youtubeId.getText().toString();
                    videoView.initialize(
                            initializedYouTubePlayer -> initializedYouTubePlayer.addListener(
                                    new com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener() {
                                        @Override
                                        public void onReady() {
                                            initializedYouTubePlayer.loadVideo(null, 0);
                                            initializedYouTubePlayer.loadVideo(videoId, 0);
                                        }
                                    }), true);
                } else {
                    videoView.setVisibility(View.INVISIBLE);
                    blank.setVisibility(View.VISIBLE);

                    Toast.makeText(AOptionAcademicMentoringPostVideo.this, "Please enter YouTube video ID first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
            }
        });

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                final String videoNameString = videoName.getText().toString();
                final String search = videoName.getText().toString().toLowerCase();
                final String ytLink = youtubeId.getText().toString();

                long time = System.currentTimeMillis();
                String destinationFileName = String.valueOf(time);
                destinationFileName += ".jpg";

                if (imageUriResultCrop != null && !TextUtils.isEmpty(videoNameString) && !TextUtils.isEmpty(ytLink)) {

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("VideoThumbnailImages");
                    final StorageReference imageFilePath = storageReference.child(destinationFileName);
                    imageFilePath.putFile(imageUriResultCrop).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownlaodLink = uri.toString();
                                    ClassYoutube post = new ClassYoutube(videoNameString,
                                            ytLink, imageDownlaodLink, search);
                                    addPost(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AOptionAcademicMentoringPostVideo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                } else {
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(AOptionAcademicMentoringPostVideo.this, "Please verify all input fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(AOptionAcademicMentoringPostVideo.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AOptionAcademicMentoringPostVideo.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(AOptionAcademicMentoringPostVideo.this, "Please accept the required permission.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(AOptionAcademicMentoringPostVideo.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else
            openGallery();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    private void addPost(ClassYoutube post) {
        final boolean[] test = {false};
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("AcademicMentoringVideos").push();

        String key = myRef.getKey();
        post.setPostKey(key);
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                test[0] = true;
                Toast.makeText(AOptionAcademicMentoringPostVideo.this, "Video Added Successfully", Toast.LENGTH_SHORT).show();
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
                finish();
            }
        });
        if (test[0]) {
            popupClickProgress.setVisibility(View.INVISIBLE);
            popupAddBtn.setVisibility(View.VISIBLE);
            Toast.makeText(AOptionAcademicMentoringPostVideo.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseSearch(String searchtext) {
        String query = searchtext.toLowerCase();
        Query firebaseQuery = databaseReference.orderByChild("search").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<ClassYoutube> options =
                new FirebaseRecyclerOptions.Builder<ClassYoutube>()
                        .setQuery(firebaseQuery, ClassYoutube.class)
                        .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ClassYoutube, YoutubeAdapter>(options) {

                    @NonNull
                    @Override
                    public YoutubeAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.youtube_row_item, parent, false);

                        return new YoutubeAdapter(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull YoutubeAdapter holder, int position, @NonNull ClassYoutube model) {
                        holder.setYoutube(model.getVideoId(), model.getTitle(), model.getImageUrl());
                        holder.setOnClicklistener(new YoutubeAdapter.Clicklistener() {
                            @Override
                            public void onItemLongClick(View view, int position) {
                                name = getItem(position).getTitle();
                                showDeleteDialog(name);
                            }
                        });
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void showDeleteDialog(final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AOptionAcademicMentoringPostVideo.this, R.style.AlertDialogBox);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this video?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog dialog = new SpotsDialog.Builder().setContext(AOptionAcademicMentoringPostVideo.this).build();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_white_card);

                Query query = databaseReference.orderByChild("title").equalTo(name);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            dataSnapshot1.getRef().removeValue();
                        }
                        dialog.dismiss();
                        Toast.makeText(AOptionAcademicMentoringPostVideo.this, "Video Deleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void startCrop(@NonNull Uri uri) {
        long time = System.currentTimeMillis();
        String destinationFileName = String.valueOf(time);
        destinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(16, 9);
        uCrop.withMaxResultSize(1000, 1000);
        uCrop.withOptions(getCropOptions());
        uCrop.start(AOptionAcademicMentoringPostVideo.this);
    }

    private UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarTitle("Crop Photo");

        return options;
    }
}