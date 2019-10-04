package com.example.securitycam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Timer;
import java.util.TimerTask;

public class Profil extends AppCompatActivity {


    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    final DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(
            firebaseUser.getUid());

    Button btnKmr,btnAlarm;
    TextView txt1,txt2;
    String kullaniciAdi,kullaniciSoyadi;
    Integer temp;
    Timer timer;
    String cevap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil);

        btnKmr=findViewById(R.id.kmr);
        btnAlarm=findViewById(R.id.alrm);
        txt1=findViewById(R.id.ad);
        txt2=findViewById(R.id.soyad);

        //databaseReference.child("kamera").setValue("0");

        Intent intent = new Intent(getApplicationContext(),BildirimServisi.class);
        startService(intent);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                kontrol();
            }

        }, 0, 1000);


        btnKmr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),BildirimServisi.class);
                stopService(intent);

                Intent i = new Intent(Profil.this,Second.class);
                startActivity(i);
            }
        });
        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profil.this,Alarm.class);
                startActivity(i);
            }
        });
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                kullaniciAdi = dataSnapshot.child("ad").getValue().toString();
                kullaniciSoyadi = dataSnapshot.child("soyad").getValue().toString();

                txt1.setText(kullaniciAdi);
                txt2.setText(kullaniciSoyadi);

                txt1.setEnabled(true);
                txt2.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void kontrol()
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cevap=dataSnapshot.child("kamera").getValue().toString();
                switch (cevap)
                {
                    case "0":
                        btnKmr.setVisibility(View.VISIBLE);
                        break;

                    case "1":
                        btnKmr.setVisibility(View.INVISIBLE);
                        break;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
