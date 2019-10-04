package com.example.securitycam;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Second extends AppCompatActivity implements SurfaceHolder.Callback {
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    public MediaRecorder mrec = new MediaRecorder();
    private Button startRecording = null;
    private Camera mCamera;
    private Context mContext;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    final DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(
            firebaseUser.getUid());
    private int sayac=0;
    private Timer myTimer=new Timer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        databaseReference.child("kamera").setValue("1");

        Log.i(null , "Video starting");
        startRecording = (Button)findViewById(R.id.buttonstart);
        mCamera = Camera.open();
        surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mContext = getApplicationContext();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mBroadcastReceiver,iFilter);

        startRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Second.this,Profil.class);
                startActivity(i);
                finish();
            }
        });


    }
    protected void startRecording() throws IOException
    {
        mrec = new MediaRecorder();
        mCamera.unlock();

        mrec.setCamera(mCamera);

        mrec.setPreviewDisplay(surfaceHolder.getSurface());
        mrec.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mrec.setAudioSource(MediaRecorder.AudioSource.MIC);

        mrec.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
        mrec.setPreviewDisplay(surfaceHolder.getSurface());
        mrec.setOutputFile("/sdcard/zzzz.3gp");

        mrec.prepare();
        mrec.start();


    }

    protected void stopRecording() {
        mrec.stop();
        mrec.release();
        mCamera.release();
    }

    private void releaseMediaRecorder(){
        if (mrec != null) {
            mrec.reset();   // clear recorder configuration
            mrec.release(); // release the recorder object
            mrec = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera != null){
            Camera.Parameters params = mCamera.getParameters();
            mCamera.setParameters(params);
        }
        else {
            finish();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
            if(isCharging)
            {
                try {
                    databaseReference.child("temp").setValue("1");
                    startRecording();
                    TimerTask gorev =new TimerTask() {

                        @Override
                        public void run() {
                            sayac++;
                            if(sayac==8)
                            {
                                releaseMediaRecorder();
                                Intent i = new Intent(Second.this,Third.class);
                                startActivity(i);
                                finish();
                                myTimer.cancel();
                            }
                        }
                    };

                    myTimer.schedule(gorev,0,1000);
                } catch (Exception e) {
                    String message = e.getMessage();
                    Log.i(null, "Problem Start"+message);
                    mrec.release();
                }
            }
        }
    };

}