package com.example.taki.firebaseauthenticatinexample;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
private EditText editText,editText1;
private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initVariable();
        loadProfile();
    }
    private void loadProfile(){
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(this,ProfileActivity.class));
            finish();
        }
    }
    private void initVariable(){
        mAuth=FirebaseAuth.getInstance();
    }
    private void initView(){
        editText=findViewById(R.id.userEmail);
        editText1=findViewById(R.id.userPassword);

    }

    public void login(View view) {


      if (mAuth.getCurrentUser()!=null){

          mAuth.getInstance().signOut();
      }

        mAuth.signInWithEmailAndPassword(editText.getText().toString().trim(),
                editText1.getText().toString().trim()).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Singup(View view) {
        startActivity(new Intent(this,Main2Activity.class));
        finish();
    }

    public void Forgetpassword(View view) {
        final String _email =editText.getText().toString().trim();

        if (_email.isEmpty()){
            editText.setError("Enter Email");
            editText.requestFocus();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Are you confirm to Reset password ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mAuth.sendPasswordResetEmail(_email).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "An Email Send to "+_email, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            }
        });
        builder.setNegativeButton("no",null);
        builder.show();
    }
}
