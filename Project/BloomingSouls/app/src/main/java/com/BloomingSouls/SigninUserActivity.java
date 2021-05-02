package com.BloomingSouls;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaeger.library.StatusBarUtil;

import java.util.Arrays;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import dmax.dialog.SpotsDialog;

public class SigninUserActivity extends AppCompatActivity {
    EditText emailId, password;
    Button btnSignIn;
    ImageView showPass, btnG, btnF;
    TextView tvSignUp, adminY, forgot, privacy;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    CallbackManager mCallbackManager;
    AuthCredential credential;
    ProgressBar loginProgress;
    AlertDialog dialogG;
    android.app.AlertDialog loader;

    int RC_SIGN_IN = 1;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_user);

        StatusBarUtil.setTransparent(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();

        loginProgress = findViewById(R.id.loginProgress);
        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        btnSignIn = findViewById(R.id.button);
        tvSignUp = findViewById(R.id.signupText);
        adminY = findViewById(R.id.adminSignin);
        showPass = findViewById(R.id.show);
        forgot = findViewById(R.id.forgotText);
        privacy = findViewById(R.id.privacy);
        btnG = findViewById(R.id.gs);
        btnF = findViewById(R.id.fs);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(SigninUserActivity.this, gso);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(
                mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                        mFirebaseAuth.signInWithCredential(credential)
                                .addOnCompleteListener(SigninUserActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            checkIfAccExists2((FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                        } else {
                                            Toast.makeText(SigninUserActivity.this, "Login Error with Facebook", Toast.LENGTH_SHORT).show();
                                            loader.dismiss();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SigninUserActivity.this, "Login Error with Facebook", Toast.LENGTH_SHORT).show();
                        loader.dismiss();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(SigninUserActivity.this, "Login Error with Facebook", Toast.LENGTH_SHORT).show();
                        loader.dismiss();
                    }
                }
        );

        loginProgress.setVisibility(View.INVISIBLE);

        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    flag = 1;
                    password.setTransformationMethod(null);
                } else {
                    flag = 0;
                    password.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgress.setVisibility(View.VISIBLE);
                btnSignIn.setVisibility(View.INVISIBLE);

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                final String email = emailId.getText().toString();
                final String pwd = password.getText().toString();

                if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(SigninUserActivity.this, "All fields are empty!", Toast.LENGTH_SHORT).show();
                    btnSignIn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                } else if (email.isEmpty()) {
                    emailId.setError("Please provide Email!");
                    emailId.requestFocus();
                    btnSignIn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter the Password!");
                    password.requestFocus();
                    btnSignIn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                } else if (!emailId.getText().toString().trim().matches(emailPattern)) {
                    emailId.setError("Please enter a valid Email!");
                    emailId.requestFocus();
                    btnSignIn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                } else if (!(email.isEmpty() && pwd.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(SigninUserActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                btnSignIn.setVisibility(View.VISIBLE);
                                loginProgress.setVisibility(View.INVISIBLE);
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidUserException invalidEmail) {
                                    AuthCredential credential = GoogleAuthProvider.getCredential(email, pwd);
                                    mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(SigninUserActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Intent intToHome = new Intent(SigninUserActivity.this, ActivityHomeUser.class);
                                                startActivity(intToHome);
                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                Toast.makeText(SigninUserActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                finish();
                                                checkIfAccExists((FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                            } else {
                                                Toast.makeText(SigninUserActivity.this, "Please enter a registered Email.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    loginProgress.setVisibility(View.INVISIBLE);
                                    btnSignIn.setVisibility(View.VISIBLE);
                                } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                    Toast.makeText(SigninUserActivity.this, "Please enter valid Password.", Toast.LENGTH_SHORT).show();
                                    loginProgress.setVisibility(View.INVISIBLE);
                                    btnSignIn.setVisibility(View.VISIBLE);
                                } catch (Exception e) {
                                    Toast.makeText(SigninUserActivity.this, "Login error, Please try again.", Toast.LENGTH_SHORT).show();
                                    loginProgress.setVisibility(View.INVISIBLE);
                                    btnSignIn.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Intent intToHome = new Intent(SigninUserActivity.this, ActivityHomeUser.class);
                                startActivity(intToHome);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                Toast.makeText(SigninUserActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SigninUserActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnSignIn.setVisibility(View.VISIBLE);
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignUp = new Intent(SigninUserActivity.this, SignupActivity.class);
                startActivity(intSignUp);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        adminY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intAdm = new Intent(SigninUserActivity.this, SigninAdminActivity.class);
                startActivity(intAdm);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intPr = new Intent(SigninUserActivity.this, PrivacyPolicy.class);
                startActivity(intPr);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loader = new SpotsDialog.Builder().setContext(SigninUserActivity.this).build();
                loader.show();
                loader.getWindow().setBackgroundDrawableResource(R.drawable.bg_white_card);

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        btnF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loader = new SpotsDialog.Builder().setContext(SigninUserActivity.this).build();
                loader.show();
                loader.getWindow().setBackgroundDrawableResource(R.drawable.bg_white_card);

                LoginManager.getInstance().logInWithReadPermissions(
                        SigninUserActivity.this,
                        Arrays.asList("email", "public_profile")
                );
            }
        });
    }

    @Override
    public void onPause() {
        if (dialogG != null && dialogG.isShowing()) {
            dialogG.dismiss();
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().getCurrentUser().delete();
                FirebaseAuth.getInstance().signOut();
            }
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogBox);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.logo_icon);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                loader.dismiss();
                account = task.getResult(ApiException.class);
                credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loader.show();
                            checkIfAccExists((FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        } else {
                            Toast.makeText(SigninUserActivity.this, "Login Error with Google", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (ApiException e) {
                Toast.makeText(this, "Login Error with Google", Toast.LENGTH_SHORT).show();
                loader.dismiss();
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkIfAccExists(String id) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("UserData");

        databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(SigninUserActivity.this, "Login with Google Successful", Toast.LENGTH_SHORT).show();
                    Intent activity = new Intent(getApplicationContext(), ActivityHomeUser.class);
                    startActivity(activity);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    loader.dismiss();
                } else {
                    popupForGoogle(id);
                    loader.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void checkIfAccExists2(String id) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("UserData");

        databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(SigninUserActivity.this, "Login with Facebook Successful", Toast.LENGTH_SHORT).show();
                    Intent activity = new Intent(getApplicationContext(), ActivityHomeUser.class);
                    startActivity(activity);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    loader.dismiss();
                } else {
                    popupForFacebook(id);
                    loader.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void popupForGoogle(String id) {
        LayoutInflater inflater = this.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(inflater.inflate(R.layout.signin_helper_google, null));
        builder.setCancelable(false);
        builder.setPositiveButton("Login", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogG.dismiss();
                FirebaseAuth.getInstance().getCurrentUser().delete();
                FirebaseAuth.getInstance().signOut();
            }
        });
        dialogG = builder.create();
        dialogG.show();

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        EditText et_name = dialogG.findViewById(R.id.editText1);
        EditText et_email = dialogG.findViewById(R.id.editText2);
        EditText et_phone = dialogG.findViewById(R.id.editText3);

        et_name.setText(account.getDisplayName());
        et_email.setText(account.getEmail());

        dialogG.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String phonePattern = "[0-9]{10}";
                String namePattern = "^[A-Za-z ]+$";

                if (et_name.getText().toString().trim().isEmpty()) {
                    et_name.setError("Please enter your Name!");
                    et_name.requestFocus();
                } else if (et_email.getText().toString().trim().isEmpty()) {
                    et_email.setError("Please enter your Email!");
                    et_email.requestFocus();
                } else if (et_phone.getText().toString().trim().isEmpty()) {
                    et_phone.setError("Please enter your Phone Number!");
                    et_phone.requestFocus();
                } else if (et_phone.getText().toString().trim().length() < 10) {
                    et_phone.setError("Please enter 10 Phone Number!");
                    et_phone.requestFocus();
                } else if (!et_name.getText().toString().trim().matches(namePattern)) {
                    et_name.setError("Please enter a valid full Name!");
                    et_name.requestFocus();
                } else if (!et_phone.getText().toString().trim().matches(phonePattern)) {
                    et_phone.setError("Please enter a valid Phone Number!");
                    et_phone.requestFocus();
                } else if (!et_email.getText().toString().trim().matches(emailPattern)) {
                    et_email.setError("Please enter a valid Email!");
                    et_email.requestFocus();
                } else {
                    String phoneText = et_phone.getText().toString().trim();
                    String name = et_name.getText().toString().trim();
                    String email = et_email.getText().toString().trim();

                    Uri photoUrl = user.getPhotoUrl();
                    String photoUrlStr = photoUrl.toString();
                    photoUrlStr = photoUrlStr.replace("s96-c", "s500-c");
                    Uri photo = Uri.parse(photoUrlStr);

                    dialogG.dismiss();
                    loader.show();

                    updateUserInfo(name, phoneText, email, photo, id, user);
                }
            }
        });
    }

    private void popupForFacebook(String id) {
        LayoutInflater inflater = this.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(inflater.inflate(R.layout.signin_helper_facebook, null));
        builder.setCancelable(false);
        builder.setPositiveButton("Login", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogG.dismiss();
                FirebaseAuth.getInstance().getCurrentUser().delete();
                FirebaseAuth.getInstance().signOut();
            }
        });
        dialogG = builder.create();
        dialogG.show();

        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        EditText et_name = dialogG.findViewById(R.id.editText1);
        EditText et_email = dialogG.findViewById(R.id.editText2);
        EditText et_phone = dialogG.findViewById(R.id.editText3);

        et_name.setText(user.getDisplayName());
        et_email.setText(user.getEmail());

        dialogG.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String phonePattern = "[0-9]{10}";
                String namePattern = "^[A-Za-z ]+$";

                if (et_name.getText().toString().trim().isEmpty()) {
                    et_name.setError("Please enter your Name!");
                    et_name.requestFocus();
                } else if (et_email.getText().toString().trim().isEmpty()) {
                    et_email.setError("Please enter your Email!");
                    et_email.requestFocus();
                } else if (et_phone.getText().toString().trim().isEmpty()) {
                    et_phone.setError("Please enter your Phone Number!");
                    et_phone.requestFocus();
                } else if (et_phone.getText().toString().trim().length() < 10) {
                    et_phone.setError("Please enter 10 Phone Number!");
                    et_phone.requestFocus();
                } else if (!et_name.getText().toString().trim().matches(namePattern)) {
                    et_name.setError("Please enter a valid full Name!");
                    et_name.requestFocus();
                } else if (!et_phone.getText().toString().trim().matches(phonePattern)) {
                    et_phone.setError("Please enter a valid Phone Number!");
                    et_phone.requestFocus();
                } else if (!et_email.getText().toString().trim().matches(emailPattern)) {
                    et_email.setError("Please enter a valid Email!");
                    et_email.requestFocus();
                } else {
                    String phoneText = et_phone.getText().toString().trim();
                    String name = et_name.getText().toString().trim();
                    String email = et_email.getText().toString().trim();

                    Uri photoUrl = user.getPhotoUrl();
                    String photoUrlStr = photoUrl.toString();
                    photoUrlStr = photoUrlStr + "?height=500";
                    Uri photo = Uri.parse(photoUrlStr);

                    dialogG.dismiss();
                    loader.show();

                    updateUserInfo(name, phoneText, email, photo, id, user);
                }
            }
        });
    }

    private void updateUserInfo(String name, String phone, String email, Uri pickedImgUri, String id, FirebaseUser currentUser) {
        ClassUserData user = new ClassUserData(
                name,
                email,
                phone
        );

        FirebaseDatabase.getInstance().getReference("UserData").child(id).setValue(user);

        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(pickedImgUri)
                .build();

        currentUser.updateProfile(profleUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent activity = new Intent(getApplicationContext(), Onboarding.class);
                    startActivity(activity);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else
                    Toast.makeText(SigninUserActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                loader.dismiss();
            }
        });
    }

    private void showForgotPasswordDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogBox);
        final EditText emailEt = new EditText(this);

        emailEt.setHint("Enter your email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(30);

        ConstraintLayout cLayout = new ConstraintLayout(this);
        cLayout.addView(emailEt);
        cLayout.setPadding(40, 10, 40, 10);

        builder.setTitle("Password Recovery");
        builder.setCancelable(false);
        builder.setView(cLayout);
        builder.setPositiveButton("Recover", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(SigninUserActivity.this, "Please enter the email.", Toast.LENGTH_SHORT).show();
                } else
                    beginRecovery(email);
            }
        });
    }

    private void beginRecovery(String email) {
        mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SigninUserActivity.this, "Password reset link is successfully sent.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SigninUserActivity.this, "Failed to send password reset link.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast toast = Toast.makeText(SigninUserActivity.this, e.getMessage(), Toast.LENGTH_LONG);
                TextView v = toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }
        });
    }
}