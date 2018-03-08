package com.example.nicholas.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Parameter;


public class MainActivity extends AppCompatActivity {

    Button button;
    private Camera camera;
    private Camera.Parameters params;
    boolean isflash;
    boolean hasflash;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hasflash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if(!hasflash){
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("error");
            alertDialog.setMessage("Sorry your device doesnot have flash");
            alertDialog.setButton(1, "ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alertDialog.show();
            return;
        }
        isflash = false;

        getcamera();



        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isflash){
                    turnoffflash();
                }else{
                    turnonflash();
                }
            }
        });

    }

    @SuppressLint("LongLogTag")
    private  void  getcamera(){
        if(camera == null || params == null){
            try{
                camera = Camera.open();
                params = camera.getParameters();
            }catch (RuntimeException e){
                Log.e("Camera error failed to open ",e.getMessage()) ;
            }
        }
    }

    private  void turnonflash(){

        if(!isflash){
            if (camera == null || params == null){
                return;
            }
            playsound();
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isflash = true;
            
            changebutton();
        }
    }
    private void turnoffflash(){
        if(isflash){
            if(camera == null || params == null){
             return;
            }
            playsound();
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isflash = false;

            changebutton();
        }

    }


    private void changebutton() {
        if(isflash){
            button.setText("Torch on");
        }else{
            button.setText("Torch off");
    }
    }

    private void playsound() {


        if(isflash){
            mp = MediaPlayer.create(MainActivity.this,R.raw.torchoff);

        }else {
            mp = MediaPlayer.create(MainActivity.this,R.raw.torchon);
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.release();
            }
        });
        mp.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash
        turnoffflash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on resume turn on the flash
        if(hasflash)
            turnonflash();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
        getcamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
