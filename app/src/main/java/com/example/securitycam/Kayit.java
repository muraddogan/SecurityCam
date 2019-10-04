package com.example.securitycam;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class Kayit extends AppCompatActivity {

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    Button btnn;
    EditText ad1, syd1, eml, pss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kayit);

        btnn = (Button) findViewById(R.id.kyt2);
        ad1 = (EditText) findViewById(R.id.ad1);
        syd1 = (EditText) findViewById(R.id.soyad1);
        eml = (EditText) findViewById(R.id.yeniMa);
        pss = (EditText) findViewById(R.id.pass);

        btnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddr = eml.getText().toString();
                String pass = pss.getText().toString();
                firebaseAuth.createUserWithEmailAndPassword(emailAddr, pass)
                        .addOnSuccessListener(Kayit.this, new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                kullaniciOlustur();
                                kullaniciGuncelle();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Yeni Kullanıcı Hatası", e.getMessage());
                            }
                        });

            }
        });
    }
    private void kullaniciGuncelle() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName("Takma Adınız")
                .setPhotoUri(null)
                .build();

        firebaseUser.updateProfile(userProfileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Güncelleme Hatası", task.getException().getMessage());
                        }
                    }
                });
    }


    private void kullaniciOlustur() {
        String add1=ad1.getText().toString();
        String sydd1=syd1.getText().toString();
        Map<String, String> yeniUser = new HashMap<String, String>();
        yeniUser.put("ad", ""+add1);
        yeniUser.put("soyad", ""+sydd1);
        yeniUser.put("temp","0");
        yeniUser.put("kamera","0");

        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("users")
                .child(firebaseAuth.getCurrentUser().getUid())
                .setValue(yeniUser);

        Toast.makeText(getApplicationContext(), "Oluşturuldu.", 2000).show();
    }
}

