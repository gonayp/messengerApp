package com.gpp.messenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText username, email, password, c_password;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference reference;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Nuevo Registro");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        c_password = findViewById(R.id.password_confirm);

        btn_register = findViewById(R.id.btn_register);


        auth = FirebaseAuth.getInstance();


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean valid = true;
                String txt_username = username.getText().toString();
                String txt_mail = email.getText().toString();
                String txt_pass = password.getText().toString();
                String txt_pass_conf= c_password.getText().toString();



                if (TextUtils.isEmpty(txt_username)) {
                    username.setError("Requiredo.");
                    valid = false;
                } else {
                    username.setError(null);
                }

                if (TextUtils.isEmpty(txt_mail)) {
                    email.setError("Requiredo.");
                    valid = false;
                } else {
                    email.setError(null);
                }

                if (TextUtils.isEmpty(txt_pass)) {
                    password.setError("Requiredo.");
                    valid = false;
                } else {
                    password.setError(null);
                }

                if (TextUtils.isEmpty(txt_pass_conf)) {
                    c_password.setError("Requiredo.");
                    valid = false;
                } else {
                    c_password.setError(null);
                }

                if(valid) {
                    //Requisitos de contraseña
                    if (txt_pass.length() < 6) {
                        password.setError("Requiredo.");
                        Toast.makeText(RegisterActivity.this, "La contraseña tiene que tener mas de 5 caracteres.", Toast.LENGTH_SHORT).show();
                        valid = false;
                    } else {
                        password.setError(null);
                    }

                    //Contraseñas iguales
                    if (!txt_pass.equals(txt_pass_conf)) {
                        password.setError("Required.");
                        c_password.setError("Required.");
                        Toast.makeText(RegisterActivity.this, "Las contraseñas deben coincidir.", Toast.LENGTH_SHORT).show();
                        valid = false;
                    } else {
                        password.setError(null);
                        c_password.setError(null);
                    }

                    if (valid) {
                        register(txt_username, txt_mail, txt_pass);
                    }
                }

            }
        });


    }

    private void register(final String username, String mail, String pass){
        auth.createUserWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username",username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");
                            hashMap.put("searchName",username.toLowerCase());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else{
                            Log.d("*******",task.getException().toString());
                            Toast.makeText(RegisterActivity.this,"Imposible hacer registro con estos datos o sin conexión a internet.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
