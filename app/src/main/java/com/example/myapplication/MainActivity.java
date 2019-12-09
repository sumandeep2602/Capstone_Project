package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Detector.Processor {



    SurfaceView surfaceView;
    TextView txtView;
    private CameraSource cameraSource;

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (Exception e) {

                    }
                }
            }
            break;
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        surfaceView = findViewById(R.id.surfaceView);
        txtView = findViewById(R.id.text_view);

        TextRecognizer txtRecognizer = new
                TextRecognizer.Builder(getApplicationContext()).build();
        if (!txtRecognizer.isOperational()) {
            Log.e("Main Activity", "Detector dependencies are not yet available");}
        else {
            cameraSource = new
                    CameraSource.Builder(getApplicationContext(), txtRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            surfaceView.getHolder().addCallback(this);
            txtRecognizer.setProcessor(this);
        }
    }


    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.CAMERA}, 1);
                return;
            }
            cameraSource.start(surfaceView.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int
            width, int height) {

    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSource.stop();
    }

    public void release() {

    }
    public void receiveDetections(Detector.Detections detections) {
        SparseArray items = detections.getDetectedItems();
        final StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            TextBlock item = (TextBlock) items.valueAt(i);
            strBuilder.append(item.getValue());
            strBuilder.append("/");
            // The following Process is used to show how to use lines & elements as well
            for (int j = 0; j < items.size(); j++) {
                TextBlock textBlock = (TextBlock) items.valueAt(j);
                strBuilder.append(textBlock.getValue());
                strBuilder.append("/");
                for (Text line : textBlock.getComponents()) {
                    //extract scanned text lines here
                    Log.v("lines", line.getValue());
                    strBuilder.append(line.getValue());
                    strBuilder.append("/");
                    for (Text element : line.getComponents()) {
                        //extract scanned text words here
                        Log.v("element", element.getValue());
                        strBuilder.append(element.getValue());
                    }
                }
            }
        }
        Log.v("strBuilder.toString()", strBuilder.toString());

        txtView.post(new Runnable() {
            @Override
            public void run() {
                txtView.setText(strBuilder.toString());
            }

        });
    }
}



            //Create the TextRecognizer
