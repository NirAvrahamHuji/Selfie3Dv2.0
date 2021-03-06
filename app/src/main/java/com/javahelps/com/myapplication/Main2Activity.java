package com.javahelps.com.myapplication;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
/**
 * Our OpenGL program's main activity
 */
public class Main2Activity extends Activity {

    private GLSurfaceView glView;   // Use GLSurfaceView

    // Call back when the activity is started, to initialize the view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        byte[] bytes = getIntent().getByteArrayExtra("bitmapbytes");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        byte[] bytes2 = getIntent().getByteArrayExtra("bitmapbytesOrg");
        Bitmap bmpOrg = BitmapFactory.decodeByteArray(bytes2, 0, bytes2.length);


        glView = new GLSurfaceView(this);// Allocate a GLSurfaceView
        glView.setRenderer(new MyGLRenderer(this, bmp, bmpOrg)); // Use a custom renderer
        this.setContentView(glView);                // This activity sets to GLSurfaceView
    }

    // Call back when the activity is going into the background
    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
    }

    // Call back after onPause()
    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }
}