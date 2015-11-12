package com.example.ash.videotransfercustomcamera;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.os.Handler;
import com.google.android.glass.view.WindowUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.media.MediaRecorder;
public class CameraManager extends Activity implements SurfaceHolder.Callback{

    static Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;;
    MediaRecorder recorder;
    String stringPath = "/sdcard/samplevideo.3gp";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!previewing) {
                    try {
                        camera = Camera.open();
                        camera.setPreviewDisplay(surfaceHolder);
                        camera.startPreview();
                    }
                    catch  (Exception e){
                        e.printStackTrace();
                        if (camera != null) {
                            camera.stopPreview();
                            camera.release();
                        }
                    }
//                    if (camera != null) {
////                            camera.setPreviewDisplay(surfaceHolder);
////                            camera.startPreview();
//                            previewing = true;
//                            String m_path = Environment.getExternalStoragePublicDirectory(
//                                    Environment.DIRECTORY_DCIM).getAbsolutePath() + "/test.mp4";
//                        recorder=new MediaRecorder();
//                        recorder.setCamera(camera);
//                        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//                        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
//                        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
//                        recorder.setOutputFile(m_path);
//                        recorder.setVideoSize(surfaceView.getWidth(), surfaceView.getHeight());
//                        recorder.setPreviewDisplay(surfaceHolder.getSurface());
//                            try{
//                                recorder.prepare();
//                                recorder.start();
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//
//                            }
//                    }
                }
            }
        }, 250);

    }
        @Override
        public boolean onCreatePanelMenu(int featureId, Menu menu) {
            if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
                getMenuInflater().inflate(R.menu.voice_menus, menu);
                return true;
            }
            // Pass through to super to setup touch menu.
            return super.onCreatePanelMenu(featureId, menu);
        }
        @Override
        public boolean onCreateOptionsMenu(android.view.Menu menu) {

            getMenuInflater().inflate(R.menu.voice_menus, menu);
            return true;
        }
        @Override
        public boolean onMenuItemSelected(int featureId, MenuItem item) {
            if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
                switch (item.getItemId()) {
                    case R.id.stop_recording_item:
                        Log.d("VOICE", "STOP");
                    if(camera != null && previewing){
                        camera.stopPreview();
                        camera.release();
                        camera = null;

                        previewing = false;
                        recorder.stop();
                        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
                        recorder.release(); // Now the object cannot be reused

                    }
                        finish();
                }

                return true;
            }
            // Good practice to pass through to super if not handled
            return super.onMenuItemSelected(featureId, item);
        }




    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDestroy() {
        if (camera != null) {
            camera.release();
        }
        super.onDestroy();
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }
}