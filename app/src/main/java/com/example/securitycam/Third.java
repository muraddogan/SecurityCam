package com.example.securitycam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.text.DecimalFormat;

public class Third extends AppCompatActivity {
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    final DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(
            firebaseUser.getUid());
    private StorageReference mStorageRef;
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    ProgressBar prg;
    TextView ttv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        prg=findViewById(R.id.progressBar);
        ttv=findViewById(R.id.textView30);

        Uri file = Uri.fromFile(new File("/sdcard/zzzz.3gp"));
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference riversRef = mStorageRef.child(currentuser+"/"+"deneme.3gp");

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),"OLDU",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Third.this,Second.class);
                        startActivity(i);
                        finish();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>(){

                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        ttv.setText("Yükleniyor: "+df2.format(progress)+"% doldu");
                        int currentprogress = (int) progress;
                        prg.setProgress(currentprogress);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(),"HATA",Toast.LENGTH_SHORT).show();
                    }
                });



    }
}
