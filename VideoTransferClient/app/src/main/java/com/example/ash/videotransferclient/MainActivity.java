package com.example.ash.videotransferclient;
import com.google.android.glass.content.Intents;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ProgressBar;
import android.content.DialogInterface;

import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.io.BufferedInputStream;
import java.io.File;
import com.google.android.glass.view.WindowUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.hardware.Camera;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;


class Params{
    public static String ipAddress = "10.20.44.173";
}

public class MainActivity extends Activity {
    private CardScrollView mCardScroller;
    private static final int TAKE_PICTURE_REQUEST = 0;
    private View mView;
    private Camera mCamera;
    @Override
    protected void onCreate(Bundle bundle) {
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        super.onCreate(bundle);

        mView = buildView();

        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return mView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }

            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        });
        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, false);
                startActivityForResult(intent, TAKE_PICTURE_REQUEST);
            }
        });
        setContentView(mCardScroller);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.voice_menus, menu);
        return true;
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            switch (item.getItemId()) {
                case R.id.start_recording_item:
                    Log.d("VOICE", "START");
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(intent, TAKE_PICTURE_REQUEST);
                    break;
            }

            return true;
        }
        // Good practice to pass through to super if not handled
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }
    /**
     * Builds a Glass styled "Hello World!" view using the {@link CardBuilder} class.
     */
    private View buildView() {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);
        card.setText("Tap to start recording");
        return card.getView();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            String videoPath = data.getStringExtra(Intents.EXTRA_VIDEO_FILE_PATH);
            new VideoSender(videoPath, this).execute();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}


class VideoSender extends AsyncTask<Void, Integer, Integer> {
    AudioManager audio;

    private String filePath;
    private Activity myActivity;
    private ProgressDialog dialog;
    private AlertDialog successDialog;
    private AlertDialog errorDialog;
    private boolean success = false;
    AlertDialog.Builder builder;
    VideoSender(String path, Activity activity){
        myActivity = activity;
        filePath = path;
        dialog = new ProgressDialog(activity);
        dialog.setTitle("Uploading...");
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgressNumberFormat(null);
        dialog.setProgressPercentFormat(null);
        dialog.setIndeterminate(false);
        dialog.setProgress(0);
    }
    @Override
    protected void onPreExecute(){
        audio  = (AudioManager) myActivity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        builder = new AlertDialog.Builder(myActivity);
        builder.setMessage("Error!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        errorDialog = builder.create();
        builder.setMessage("Success!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        successDialog = builder.create();
        dialog.show();
    }
    @Override
    protected void onPostExecute(Integer result) {
        dialog.dismiss();
        if (success){
            audio.playSoundEffect(Sounds.SUCCESS);
        }
        else{
            errorDialog.show();
        }
        if (result == -1) {
            System.exit(0);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        dialog.setProgress(progress[0]);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        Socket s = null;
        ObjectOutputStream get = null;
        try {
            try {
                s = new Socket(Params.ipAddress, 8888);
            }
            catch (ConnectException connectionFailed){
                System.out.println("Failed to connect!");
                return 0;
            }
            get = new ObjectOutputStream(s.getOutputStream());
            long u = 0;
            long fileLength = new File(filePath).length();
            FileInputStream fileInputStream  = new FileInputStream(filePath);
            byte[] buf=new byte[8192];
            int bytesread = 0, bytesBuffered = 0;
            while( (bytesread = fileInputStream.read( buf )) > -1 ) {
                get.write( buf, 0, bytesread );
                bytesBuffered += bytesread;
                u = u + bytesread;
                publishProgress((int) (u * 100 / fileLength));
                if (bytesBuffered > 1024 * 1024) { //flush after 1MB
                    bytesBuffered = 0;
                    get.flush();
                }
            }
            success = true;
            if (get != null) {
                get.close();
                get.flush();
            }
            if (s != null) {
                s.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {

        }
        return 0;
    }
}