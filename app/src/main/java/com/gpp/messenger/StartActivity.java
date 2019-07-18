 package com.gpp.messenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.gpp.messenger.Notificaciones.MyFirebaseMessagingService;

import java.util.HashMap;

 public class StartActivity extends AppCompatActivity {

    Button login, register;

    FirebaseUser firebaseUser;

     @Override
     protected void onStart() {
         super.onStart();

         firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

         if(firebaseUser != null){

             FirebaseInstanceId.getInstance().getInstanceId()
                     .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                         @Override
                         public void onComplete(@NonNull Task<InstanceIdResult> task) {
                             if (!task.isSuccessful()) {
                                 Log.w("ERROR", "getInstanceId failed", task.getException());
                                 return;
                             }

                             // Get new Instance ID token
                             String token = task.getResult().getToken();

                             DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                             HashMap<String, Object> map = new HashMap<>();
                             map.put("token", token);
                             reference.updateChildren(map);
                         }
                     });



             Intent service = new Intent(this, MyFirebaseMessagingService.class);
             startService(service);

             Intent intent = new Intent(StartActivity.this,MainActivity.class);
             startActivity(intent);
             finish();
         }

     }

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);



        login = findViewById(R.id.btn_login);
        register = findViewById(R.id.btn_registro);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (StartActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (StartActivity.this, RegisterActivity.class));
            }
        });

    }
}
