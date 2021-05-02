package com.BloomingSouls;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.jaeger.library.StatusBarUtil;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SigninAdminActivity extends AppCompatActivity {
    EditText emailId, password, phone1;
    Button btnSignIn;
    TextView adminN;
    ImageView showPass;
    FirebaseAuth mFirebaseAuth;
    ProgressBar loginProgress;

    String SecPhone = "1234567890";
    String SecEmail = "admin@bloomingsouls.com";

    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_admin);

        StatusBarUtil.setTransparent(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        loginProgress = findViewById(R.id.loginProgress);
        emailId = findViewById(R.id.editText2);
        password = findViewById(R.id.editText3);
        phone1 = findViewById(R.id.editText1);
        btnSignIn = findViewById(R.id.button);
        adminN = findViewById(R.id.userSignin);
        showPass = findViewById(R.id.show);

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
                final String phone = phone1.getText().toString();

                if (email.isEmpty() && pwd.isEmpty() && phone.isEmpty()) {
                    Toast.makeText(SigninAdminActivity.this, "All fields are empty!", Toast.LENGTH_SHORT).show();
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
                } else if (phone.isEmpty()) {
                    phone1.setError("Please provide Phone Number!");
                    phone1.requestFocus();
                    btnSignIn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                } else if (!emailId.getText().toString().trim().matches(emailPattern)) {
                    emailId.setError("Please enter a valid Email!");
                    emailId.requestFocus();
                    btnSignIn.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                } else if (!(email.isEmpty() && pwd.isEmpty() && pwd.isEmpty())) {
                    if (email.equals(SecEmail) && phone.equals(SecPhone)) {
                        mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(SigninAdminActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    btnSignIn.setVisibility(View.VISIBLE);
                                    loginProgress.setVisibility(View.INVISIBLE);
                                    try {
                                        throw Objects.requireNonNull(task.getException());
                                    } catch (FirebaseAuthInvalidUserException invalidEmail) {
                                        Toast.makeText(SigninAdminActivity.this, "Please enter registered Admin Email.", Toast.LENGTH_SHORT).show();
                                        loginProgress.setVisibility(View.INVISIBLE);
                                        btnSignIn.setVisibility(View.VISIBLE);
                                    } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                        Toast.makeText(SigninAdminActivity.this, "Please enter valid Password.", Toast.LENGTH_SHORT).show();
                                        loginProgress.setVisibility(View.INVISIBLE);
                                        btnSignIn.setVisibility(View.VISIBLE);
                                    } catch (Exception e) {
                                        Toast.makeText(SigninAdminActivity.this, "Login error, Please try again.", Toast.LENGTH_SHORT).show();
                                        loginProgress.setVisibility(View.INVISIBLE);
                                        btnSignIn.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Intent intToHome = new Intent(SigninAdminActivity.this, ActivityHomeAdmin.class);
                                    startActivity(intToHome);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    Toast.makeText(SigninAdminActivity.this, "Admin: Login Successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(SigninAdminActivity.this, "Please enter correct Admin details.", Toast.LENGTH_SHORT).show();
                        loginProgress.setVisibility(View.INVISIBLE);
                        btnSignIn.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(SigninAdminActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnSignIn.setVisibility(View.VISIBLE);
                }
            }
        });

        adminN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignUp = new Intent(SigninAdminActivity.this, SigninUserActivity.class);
                startActivity(intSignUp);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
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
}