package com.threepointogames.esk8ph.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationtutorial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.threepointogames.esk8ph.LocalSaveData;

import static com.threepointogames.esk8ph.StringReplacer.EncodeString;

public class RegisterActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private DatabaseReference databasePartnerReference;
    EditText mUsername,mEmail,mPassword,mMobileNumber;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUsername = findViewById(R.id.userNameEditText);
        mEmail = findViewById(R.id.emailEditText);
        mPassword=findViewById(R.id.passwordEditText);
        mMobileNumber=findViewById(R.id.mobileNumberEditText);
        mRegisterBtn=findViewById(R.id.registerButton);
        mLoginBtn=findViewById(R.id.loginBtn);

        fAuth=FirebaseAuth.getInstance();
        mProgressBar=findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));;
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required.");
                    return;
                }

                if(password.length() <6){
                    mPassword.setError("Password must be >=6 Characters");
                    return;
                }
                mProgressBar.setVisibility(view.VISIBLE);
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                            databaseReference.child(EncodeString(fAuth.getUid())).child("Id").setValue(fAuth.getUid());
                            databaseReference.child(EncodeString(fAuth.getUid())).child("Username").setValue(mUsername.getText().toString().trim());
                            databaseReference.child(EncodeString(fAuth.getUid())).child("Email").setValue(EncodeString(email));
                            databaseReference.child(EncodeString(fAuth.getUid())).child("Mobile_Number").setValue(mMobileNumber.getText().toString().trim());
                            databaseReference.child(EncodeString(fAuth.getUid())).child("ShareLocation").setValue("False");
                            databaseReference.child(EncodeString(fAuth.getUid())).child("Location").child("latitude").setValue("");
                            databaseReference.child(EncodeString(fAuth.getUid())).child("Location").child("longitude").setValue("");
                            LocalSaveData.saveData(RegisterActivity.this,"UsersPref","UserID",fAuth.getUid());


                            startActivity(new Intent(getApplicationContext(),MainActivity.class));;
                        }else{
                            Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));;

            }
        });

    }

}