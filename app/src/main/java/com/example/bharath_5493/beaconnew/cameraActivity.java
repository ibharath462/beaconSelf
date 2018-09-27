package com.example.bharath_5493.beaconnew;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;


public class cameraActivity extends Activity implements SurfaceHolder.Callback{

    Button myButton;
    Camera mCamera;
    MediaRecorder mediaRecorder;
    SurfaceHolder surfaceHolder;
    boolean recording;
    android.widget.ProgressBar ProgressBar;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recording = false;
        this.mCamera = Camera.open();
        mediaRecorder = new MediaRecorder();
        initMediaRecorder();

        setContentView(R.layout.activity_camera);

        SurfaceView myVideoView = (SurfaceView)findViewById(R.id.videoview);
        surfaceHolder = myVideoView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        myButton = (Button)findViewById(R.id.mybutton);
        ProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        myButton.setOnClickListener(myButtonOnClickListener);


        ProgressBar.setVisibility(View.INVISIBLE);

    }

    private Button.OnClickListener myButtonOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            if(recording){
                //mediaRecorder.stop();
                //mediaRecorder.release();
                stopRecording();
                //finish();
                myButton.setText("START");
            }else{
                //mediaRecorder.start();
                recording = true;
                try {
                    startRecording();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myButton.setText("STOP");
            }
        }};

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        prepareMediaRecorder();
        if(recording){
            stopRecording();
            myButton.setText("START");
        }else{

            recording = true;
            try {
                startRecording();
            } catch (IOException e) {
                e.printStackTrace();
            }
            myButton.setText("STOP");
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    private void initMediaRecorder(){
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        CamcorderProfile camcorderProfile_HQ = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mediaRecorder.setProfile(camcorderProfile_HQ);
        mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory()+"/myvideo.mp4");
        mediaRecorder.setMaxDuration(6000); // Set max duration 60 sec.
        mediaRecorder.setMaxFileSize(5000000); // Set max file size 5M
    }

    private void prepareMediaRecorder(){
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static class RetrofitClientInstance {
        private static final String BASE_URL = "https://api.telegram.org/bot645910887:AAEvlJ6msD4Mmr1qkW4bNfLHw-71T7ZjqVw/";
        static Retrofit retrofit;

        public static Retrofit getRetrofitInstance() {
            if (retrofit == null) {
                retrofit = new Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            }
            return retrofit;
        }
    }


    protected void startRecording() throws IOException {
        this.mediaRecorder = new MediaRecorder();
        this.mCamera.unlock();
        this.mediaRecorder.setCamera(this.mCamera);
        this.mediaRecorder.setPreviewDisplay(this.surfaceHolder.getSurface());
        this.mediaRecorder.setVideoSource(1);
        this.mediaRecorder.setAudioSource(1);
        this.mediaRecorder.setProfile(CamcorderProfile.get(1));
        this.mediaRecorder.setPreviewDisplay(this.surfaceHolder.getSurface());
        MediaRecorder mediaRecorder = this.mediaRecorder;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory());
        stringBuilder.append("/DCIM/Camera/jar.mp4");
        mediaRecorder.setOutputFile(stringBuilder.toString());
        this.mediaRecorder.setMaxDuration(50000);
        this.mediaRecorder.prepare();
        this.mediaRecorder.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRecording();
            }
        }, 5000);

    }

    protected void stopRecording() {
        recording = false;
        Toast.makeText(getApplicationContext(), "Sending video, please wait....", Toast.LENGTH_LONG).show();
        this.mediaRecorder.stop();
        this.mediaRecorder.release();
        this.mCamera.release();
        Log.d("HIII", "Stopped");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStorageDirectory());
        stringBuilder.append("/DCIM/Camera/jar.mp4");
        File file = new File(stringBuilder.toString());
        final SharedPreferences prefs = getSharedPreferences("f1nd.initial.bharath.newUI", Context.MODE_PRIVATE);
        ProgressBar.animate();
        ProgressBar.setVisibility(View.VISIBLE);
        ((GetDataService) RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class)).uploadAttachment(MultipartBody.Part.createFormData("document", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))).enqueue(new Callback<simple>(){

            @Override
            public void onResponse(Call<simple> call, Response<simple> response) {
                prefs.edit().putBoolean("camera",false).apply();

                Toast.makeText(getApplicationContext(), "Video sent success", Toast.LENGTH_SHORT).show();
                ProgressBar.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onFailure(Call<simple> call, Throwable t) {
                prefs.edit().putBoolean("camera",false).apply();
                Toast.makeText(getApplicationContext(), "Video SENT", Toast.LENGTH_SHORT).show();

                ProgressBar.setVisibility(View.INVISIBLE);
                finish();
            }
        });
    }
}
