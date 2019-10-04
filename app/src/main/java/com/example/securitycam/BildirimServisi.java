package com.example.securitycam;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class BildirimServisi extends Service {

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    final DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(
            firebaseUser.getUid());

    Context context ;
    Timer timer;
    String cevap;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate() {

        context = getApplicationContext();
        Toast.makeText(this, "Servis Çalıştı.Bu Mesaj Servis Class'dan", Toast.LENGTH_LONG).show();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                kontrol();
            }

        }, 0, 60000);
    }

    public void kontrol()
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cevap=dataSnapshot.child("temp").getValue().toString();
                Toast.makeText(BildirimServisi.this, "onDatahChange: "+cevap, Toast.LENGTH_LONG).show();
                switch (cevap)
                {
                    case "1":
                        databaseReference.child("temp").setValue("0");
                        bildirimGonder();
                        break;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(BildirimServisi.this, "olmdaı: "+cevap, Toast.LENGTH_LONG).show();
            }
        });
    }



    public void bildirimGonder(){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(BildirimServisi.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ALARM")
                .setContentText("Alarm Devreye Girdi!");

        Intent notificationIntent = new Intent(BildirimServisi.this, Alarm.class);
        PendingIntent contentIntent = PendingIntent.getActivity(BildirimServisi.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        long[] v = {500,1000};
        builder.setVibrate(v);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        manager.notify(0, builder.build());

    }

    @Override
    public void onDestroy() {
        timer.cancel();
        Toast.makeText(this, "Servis Durduruldu.Bu Mesaj Servis Class'dan", Toast.LENGTH_LONG).show();
    }

}
