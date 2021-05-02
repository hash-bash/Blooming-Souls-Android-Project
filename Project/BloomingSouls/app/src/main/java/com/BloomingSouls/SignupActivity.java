package com.BloomingSouls;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaeger.library.StatusBarUtil;
import com.yalantis.ucrop.UCrop;
import com.ybs.passwordstrengthmeter.PasswordStrength;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SignupActivity extends AppCompatActivity implements TextWatcher {
    static int PReqCode = 1;
    static int REQUESCODE = 1;

    Button btnSignUp;
    TextView tvSignIn;
    DatabaseReference databaseReference;
    ImageView ImgUserPhoto, showPass1;
    Uri pickedImgUri;
    EditText emailId, password, password2, name1, phone1;
    ProgressBar loadingProgress;
    Uri imageUriResultCrop;

    int flag = 0;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_user);

        StatusBarUtil.setTransparent(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editText3);
        password = findViewById(R.id.editText4);
        password2 = findViewById(R.id.editText5);
        btnSignUp = findViewById(R.id.button);
        tvSignIn = findViewById(R.id.textView);
        name1 = findViewById(R.id.editText1);
        phone1 = findViewById(R.id.editText2);
        loadingProgress = findViewById(R.id.signup_progress);
        ImgUserPhoto = findViewById(R.id.imageView1);
        showPass1 = findViewById(R.id.show);

        databaseReference = FirebaseDatabase.getInstance().getReference("UserData");

        loadingProgress.setVisibility(View.INVISIBLE);

        password.addTextChangedListener(this);

        showPass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    flag = 1;
                    password.setTransformationMethod(null);
                    password2.setTransformationMethod(null);
                } else {
                    flag = 0;
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    password2.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignUp.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);

                final String email = emailId.getText().toString();
                final String pwd = password.getText().toString();
                final String name = name1.getText().toString();
                final String phone = phone1.getText().toString();
                final String pwd2 = password2.getText().toString();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String passwordPattern = "^(.{0,7}|[^0-9]*|[a-zA-Z0-9]*)$";
                String phonePattern = "[0-9]{10}";
                String namePattern = "^[A-Za-z ]+$";

                if (email.isEmpty() && pwd.isEmpty() && name.isEmpty() && phone.isEmpty()) {
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignupActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty()) {
                    btnSignUp.setVisibility(View.INVISIBLE);
                    loadingProgress.setVisibility(View.VISIBLE);
                    name1.setError("Please enter your Name!");
                    name1.requestFocus();
                } else if (phone.isEmpty()) {
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    phone1.setError("Please enter your Phone Number!");
                    phone1.requestFocus();
                } else if (email.isEmpty()) {
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    emailId.setError("Please provide Email!");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    password.setError("Please enter the Password!");
                    password.requestFocus();
                } else if (!name1.getText().toString().trim().matches(namePattern)) {
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    name1.setError("Please enter a valid full Name!");
                    name1.requestFocus();
                } else if (!phone1.getText().toString().trim().matches(phonePattern)) {
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    phone1.setError("Please enter a valid Phone Number!");
                    phone1.requestFocus();
                } else if (!emailId.getText().toString().trim().matches(emailPattern)) {
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    emailId.setError("Please enter a valid Email!");
                    emailId.requestFocus();
                } else if (phone1.length() < 10 || phone1.length() > 10) {
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    phone1.setError("Enter 10 digits Phone Number!");
                    phone1.requestFocus();
                } else if (pickedImgUri == null) {
                    Toast.makeText(SignupActivity.this, "Please select Profile Picture.", Toast.LENGTH_SHORT).show();
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else if (password.length() < 8) {
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    password.setError("Minimum length of Password should be 8!");
                    password.requestFocus();
                } else if (password.getText().toString().trim().matches(passwordPattern)) {
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    Toast toast = Toast.makeText(SignupActivity.this, "Password must contain at least one number and one special character.", Toast.LENGTH_LONG);
                    TextView v2 = toast.getView().findViewById(android.R.id.message);
                    if (v2 != null) v2.setGravity(Gravity.CENTER);
                    toast.show();
                    password.requestFocus();
                } else if (!pwd.equals(pwd2)) {
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignupActivity.this, "Password does not match.", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    password2.setText("");
                } else if (!(email.isEmpty() && pwd.isEmpty() && name.isEmpty() && phone.isEmpty())) {
                    btnSignUp.setVisibility(View.INVISIBLE);
                    loadingProgress.setVisibility(View.VISIBLE);
                    createUserAccount(email, name, pwd, phone);
                } else {
                    btnSignUp.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignupActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inti = new Intent(SignupActivity.this, SigninUserActivity.class);
                startActivity(inti);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });
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
                if (imageUriResultCrop != null) {
                    ImgUserPhoto.setImageURI(imageUriResultCrop);
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        }
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
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(
            CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updatePasswordStrengthView(s.toString());
    }

    private void updatePasswordStrengthView(String password) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView strengthView = findViewById(R.id.password_strength);
        if (TextView.VISIBLE != strengthView.getVisibility())
            return;

        if (password.isEmpty()) {
            strengthView.setText("");
            progressBar.setProgress(0);
            return;
        }

        String check = password + "a";
        PasswordStrength str = PasswordStrength.calculateStrength(check);
        strengthView.setText(str.getText(this));
        strengthView.setTextColor(str.getColor());

        progressBar.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        if (str.getText(this).equals("Weak")) {
            progressBar.setProgress(25);
        } else if (str.getText(this).equals("Medium")) {
            progressBar.setProgress(50);
        } else if (str.getText(this).equals("Strong")) {
            progressBar.setProgress(75);
        } else {
            progressBar.setProgress(100);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(SignupActivity.this, "Please accept the required permission.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(SignupActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else
            openGallery();
    }

    private void createUserAccount(final String email, final String name, final String pwd, final String phone) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateUserInfo(name, phone, email, pwd, mFirebaseAuth.getCurrentUser());
                        } else {
                            btnSignUp.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "You are already registered.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignupActivity.this, "Sign up unsuccessful, Please try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void updateUserInfo(String name, String phone, String email, String pwd, FirebaseUser currentUser) {
        ClassUserData user = new ClassUserData(
                name,
                email,
                phone,
                pwd
        );

        FirebaseDatabase.getInstance().getReference("UserData")
                .child((FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
            }
        });

        long time = System.currentTimeMillis();
        String destinationFileName = String.valueOf(time);
        destinationFileName += ".jpg";

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("ProfilePictures");
        StorageReference imageFilePath = mStorage.child(destinationFileName);

        imageFilePath.putFile(imageUriResultCrop).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profleUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent activity = new Intent(getApplicationContext(), Onboarding.class);
                                            startActivity(activity);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                            finish();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void startCrop(@NonNull Uri uri) {
        long time = System.currentTimeMillis();
        String destinationFileName = String.valueOf(time);
        destinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(500, 500);
        uCrop.withOptions(getCropOptions());
        uCrop.start(SignupActivity.this);
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