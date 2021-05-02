package com.BloomingSouls;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.barteksc.pdfviewer.PDFView;
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

public class AOptionPersonalityDevelopmentPostPdf extends AppCompatActivity {
    private static final int PICK_PDF_CODE = 1;
    private static final int PReqCode = 2;
    private static final int REQUEST_CODE = 2;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Dialog popAddPost;
    ImageView popupAddBtn;
    TextView popupTitle;
    ProgressBar popupClickProgress;
    Button chooseBtn, thumbBtn;
    String name;
    RecyclerView recyclerView;
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    NightModeSharedPref sharedpref;
    PDFView pdf;
    Uri imageUriResultCrop, pdfUri;
    FirebaseRecyclerAdapter<ClassPdf, PdfAdapter> firebaseRecyclerAdapter;

    private Uri pickedImgUri = null;

    @SuppressLint("SetJavaScriptEnabled")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new NightModeSharedPref(this);
        if (sharedpref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_post_pdf);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("PersonalityDevelopmentPdf");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerview_ShowPdf);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        iniPopup();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });

        FirebaseRecyclerOptions<ClassPdf> options =
                new FirebaseRecyclerOptions.Builder<ClassPdf>()
                        .setQuery(mDatabaseReference, ClassPdf.class)
                        .build();

        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).build();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_white_card);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ClassPdf, PdfAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PdfAdapter holder, int position, @NonNull ClassPdf model) {
                holder.setPdf(model.getPdfUrl(), model.getName(), model.getImageUrl());
                holder.setOnClicklistener(new PdfAdapter.Clicklistener() {
                    @Override
                    public void onItemLongClick(View view, int position) {
                        name = getItem(position).getName();
                        showDeleteDialog(name);
                    }
                });
                dialog.dismiss();
            }

            @NonNull
            @Override
            public PdfAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pdf_row_item, parent, false);
                return new PdfAdapter(view);
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
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                pdfUri = data.getData();
            } else {
                Toast.makeText(this, "No File Chosen", Toast.LENGTH_SHORT).show();
            }
            pdf.fromUri(pdfUri).load();
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            pickedImgUri = data.getData();
            if (pickedImgUri != null) {
                startCrop(pickedImgUri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                imageUriResultCrop = UCrop.getOutput(data);
            } else {
                Intent intent = new Intent(getApplicationContext(), AOptionPersonalityDevelopmentPostPdf.class);
                startActivity(intent);
            }
        }
    }

    private void firebaseSearch(String searchtext) {
        String query = searchtext.toLowerCase();
        Query firebaseQuery = mDatabaseReference.orderByChild("search").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<ClassPdf> options =
                new FirebaseRecyclerOptions.Builder<ClassPdf>()
                        .setQuery(firebaseQuery, ClassPdf.class)
                        .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ClassPdf, PdfAdapter>(options) {

                    @NonNull
                    @Override
                    public PdfAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.pdf_row_item, parent, false);

                        return new PdfAdapter(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull PdfAdapter holder, int position, @NonNull ClassPdf model) {
                        holder.setPdf(model.getPdfUrl(), model.getName(), model.getImageUrl());
                        holder.setOnClicklistener(new PdfAdapter.Clicklistener() {
                            @Override
                            public void onItemLongClick(View view, int position) {
                                name = getItem(position).getName();
                                showDeleteDialog(name);
                            }
                        });
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void showDeleteDialog(final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AOptionPersonalityDevelopmentPostPdf.this, R.style.AlertDialogBox);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this Pdf?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog dialog = new SpotsDialog.Builder().setContext(AOptionPersonalityDevelopmentPostPdf.this).build();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_white_card);

                Query query = mDatabaseReference.orderByChild("name").equalTo(name);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            dataSnapshot1.getRef().removeValue();
                        }
                        dialog.dismiss();
                        Toast.makeText(AOptionPersonalityDevelopmentPostPdf.this, "Pdf Deleted", Toast.LENGTH_SHORT).show();
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

    private void getPDF() {
        if (ContextCompat.checkSelfPermission(AOptionPersonalityDevelopmentPostPdf.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AOptionPersonalityDevelopmentPostPdf.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(AOptionPersonalityDevelopmentPostPdf.this, "Please accept the required permission.", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(AOptionPersonalityDevelopmentPostPdf.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else {
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_CODE);
        }
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Please accept the required permission.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else
            openGallery();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_CODE);
    }

    private void iniPopup() {
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_pdf_add_new);

        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);
        chooseBtn = popAddPost.findViewById(R.id.choose_btn);
        thumbBtn = popAddPost.findViewById(R.id.thumbnail);
        pdf = popAddPost.findViewById(R.id.pdf_view);

        popupAddBtn.setVisibility(View.VISIBLE);
        popupClickProgress.setVisibility(View.INVISIBLE);

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPDF();
            }
        });

        thumbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestForPermission();
            }
        });

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                if (!popupTitle.getText().toString().isEmpty()
                        && pdfUri != null && pickedImgUri != null) {

                    long time = System.currentTimeMillis();
                    String destinationFileName = String.valueOf(time);
                    destinationFileName += ".jpg";

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("PdfThumbnailImages");
                    final StorageReference imageFilePath = storageReference.child(destinationFileName);
                    final StorageReference sRef = mStorageReference.child("PersonalityDevelopmentPdf/" + System.currentTimeMillis() + ".pdf");

                    imageFilePath.putFile(imageUriResultCrop).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri1) {
                                    String imageDownlaodLink = uri1.toString();
                                    sRef.putFile(pdfUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @SuppressWarnings("VisibleForTests")
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    popupAddBtn.setVisibility(View.VISIBLE);
                                                    popupClickProgress.setVisibility(View.INVISIBLE);

                                                    Toast.makeText(getApplicationContext(), "Pdf Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                    sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri2) {
                                                            String downloadUrl = uri2.toString();
                                                            ClassPdf upload2 = new ClassPdf(popupTitle.getText().toString(), popupTitle.getText().toString().toLowerCase(), downloadUrl, imageDownlaodLink);
                                                            mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(upload2);
                                                        }
                                                    });
                                                    popAddPost.dismiss();
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    popupAddBtn.setVisibility(View.VISIBLE);
                                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AOptionPersonalityDevelopmentPostPdf.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Please verify all input fields.", Toast.LENGTH_SHORT).show();
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void startCrop(@NonNull Uri uri) {
        long time = System.currentTimeMillis();
        String destinationFileName = String.valueOf(time);
        destinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(14, 9);
        uCrop.withMaxResultSize(1000, 1000);
        uCrop.withOptions(getCropOptions());
        uCrop.start(this);
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
