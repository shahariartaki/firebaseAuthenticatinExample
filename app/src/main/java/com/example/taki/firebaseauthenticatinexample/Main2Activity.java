package com.example.taki.firebaseauthenticatinexample;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class Main2Activity extends AppCompatActivity {
private EditText user,pass,conpass;
private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        initVariable();
    }
    private void initVariable(){
        mAuth=FirebaseAuth.getInstance();

    }

    private void initView(){
        user=findViewById(R.id.userEmailsing);
        pass=findViewById(R.id.userPasswordsing);
        conpass=findViewById(R.id.usercofirmpasswordsing);
    }

    public void lohinpage(View view) {
        startActivity(new Intent(this,MainActivity.class));
        finish();

       // if (!validate()) return;
       // singUpFirebase();
    }

    private boolean validate() {
        if (user.getText().toString().trim().isEmpty()){
            user.setError("Pleasw enter Email");
            user.requestFocus();
            return  false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(user.getText().toString().trim()).matches()){
            user.setError("please Enter Valid Email");
            user.requestFocus();
            return false;
        }

        if (pass.getText().toString().trim().isEmpty()){
            pass.setError("please Enter pass ");
            pass.requestFocus();
            return false;
        }
        if (pass.getText().toString().trim().length()<6){
            pass.setText("password must be in six digit");
            pass.requestFocus();
        }

        if (!pass.getText().toString().trim().equals(conpass.getText().toString().trim())){
            conpass.setError("Password not match");
            conpass.requestFocus();
            return false;
        }

        return true;
    }
//sing up to fire base
    private void singUpFirebase() {
        mAuth.createUserWithEmailAndPassword(user.getText().toString().trim(),pass.getText().toString().trim())
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Main2Activity.this, "Singup Successfull", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthUserCollisionException){
                    Toast.makeText(Main2Activity.this, "Already Registered", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Main2Activity.this, "Faild to Sing up", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void singupbutton(View view) {

        if (!validate()) return;
        singUpFirebase();
       // startActivity(new Intent(this,MainActivity.class));
        //finish();
    }
}
