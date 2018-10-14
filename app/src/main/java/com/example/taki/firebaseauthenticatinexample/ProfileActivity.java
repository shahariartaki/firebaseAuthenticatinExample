package com.example.taki.firebaseauthenticatinexample;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    private TextView name, email,varified;
    private EditText updatename;
    private ImageView photo;
    ImageButton enableBtn;
    private Button editBtn;
    private static final int IMAGE_CHOSER = 1010;
    private Uri uri;
    StorageReference sReference;
    private String downloadUri;
    private ProgressDialog dialog;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        addListener();
        initVariable();
        loadprofileInfo();
    }
    private void loadprofileInfo(){
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser.isEmailVerified()){
            varified.setText("Varified");

        }

        else {
            varified.setText("Not Varified");
        }
        if (firebaseUser!=null){

            email.setText(firebaseUser.getEmail().toString());

            if (firebaseUser.getDisplayName()!=null)
            name.setText(firebaseUser.getDisplayName().toString());

            if (firebaseUser.getPhotoUrl()!=null) {
                String uri = firebaseUser.getPhotoUrl().toString();
                Glide.with(this).load(uri).into(photo);
            }
        }
    }

    private void addListener() {
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_CHOSER);
            }
        });

        enableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setVisibility(View.GONE);
                updatename.setVisibility(View.VISIBLE);
            }
        });
        varified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new AlertDialog.Builder(ProfileActivity.this).
                        setMessage("Verified? ").setCancelable(false)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseUser.sendEmailVerification();
                            }
                        }).setNegativeButton("No",null).show();


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IMAGE_CHOSER && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            selectImage();
        }

        //super.onActivityResult(requestCode, resultCode, data);
    }

    private void selectImage() {
        if (uri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                photo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void initView() {
        name = findViewById(R.id.username);
        varified = findViewById(R.id.isVarivied);
        email = findViewById(R.id.userEmail);
        updatename = findViewById(R.id.usernameEdit);
        editBtn = findViewById(R.id.updateprofile);
        photo = findViewById(R.id.userphoto);
        enableBtn = findViewById(R.id.enableEdit);
        dialog = new ProgressDialog(this);

    }

    private void initVariable() {
        sReference = FirebaseStorage.getInstance().getReference("USER_PHOTO");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth=FirebaseAuth.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String UserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toLowerCase();
            email.setText(UserEmail);


        }

    }


    public void UpdateProfile(View view) {
        dialog.setMessage("Uploading......");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        String imageName = System.currentTimeMillis() + ".jpg";
        sReference.child(imageName).putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUri = taskSnapshot.getDownloadUrl().toString();
                        updateTheUser();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                if (dialog.isShowing()) dialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                dialog.setProgress(progress);
                if (progress == 100)
                    if (dialog.isShowing())
                        dialog.dismiss();
            }
        });

    }


    private void updateTheUser() {
        if (updatename.getText().toString().trim().isEmpty()) {
            updatename.setError("please enter name");
            updatename.requestFocus();
            return;
        }
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder().
                setPhotoUri(Uri.parse(downloadUri)).setDisplayName(updatename.getText().toString().trim())
                .build();
        firebaseUser.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Profile Updateed", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(ProfileActivity.this, "profile not updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.logOut){

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this,MainActivity.class));
            finish();
            return true;
        }
        if (item.getItemId()==R.id.Resetpass){
            resetThePassword();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void resetThePassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view= getLayoutInflater().inflate(R.layout.resetpassword,null);

        final EditText oldpass = view.findViewById(R.id.oldpass);
        final EditText newpass = view.findViewById(R.id.newpass);
        final EditText ConfirmNewpass = view.findViewById(R.id.ConfirmNewpass);
        Button button=view.findViewById(R.id.ResetPassword);
        builder.setView(view);
        builder.show();
        final AlertDialog dialog =builder.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _email=firebaseUser.getEmail().toString();
                String _oldpass=oldpass.getText().toString().trim();
                final String _newpass=newpass.getText().toString().trim();
                String _confirm=ConfirmNewpass.getText().toString().trim();

                if (!_newpass.equals(_confirm)){
                    ConfirmNewpass.setError("Password not Match");
                    ConfirmNewpass.requestFocus();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(_email,_oldpass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    firebaseUser.updatePassword(_newpass)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(ProfileActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                    dialog.dismiss();
                                                }
                                            });
                                }
                            }
                        });
            }
        });

    }
}
