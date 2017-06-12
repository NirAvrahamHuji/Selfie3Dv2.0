package com.javahelps.com.myapplication;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
/**
 *  OpenGL Custom renderer used with GLSurfaceView
 */
class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final int MIN_ANGLE = -40;
    private static final int MAX_ANGLE = 40;
    private Cube cube;          // (NEW)
    private Bitmap depthImage;

    private static float angleCube = 0;    // Rotational angle in degree for cube (NEW)
    private static float speedCube = -15.0f;   // Rotational speed for cube (NEW)

    // Constructor
    MyGLRenderer(Context context, Bitmap depthImage , Bitmap orgImage) {
        // Set up the buffers for these shapes
        cube = new Cube(context , depthImage, orgImage);
    }


    // Call back when the surface is first created or re-created
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // Set color's clear-value to black
        gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
        gl.glClearColor(1.0f,1.0f,1.0f,1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
        gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
        gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
        gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance

    }

    // Call back after onSurfaceCreated() or whenever the window's size changes
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;   // To prevent divide by zero
        float aspect = (float)width / height;

        // Set the viewport (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
        gl.glLoadIdentity();                 // Reset projection matrix
        // Use perspective projection
        GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
        gl.glLoadIdentity();                 // Reset

    }

    // Call back to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear color and depth buffers
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Draw the pyramid (NEW)
//TODO:WORKS!!!!
//        // ----- Render the Color Cube -----
//        gl.glLoadIdentity();                // Reset the model-view matrix
//        gl.glTranslatef(-0.5f, 0.67f, -6.0f); // Translate right and into the screen
////        gl.glScalef(1.0f, 1.0f, 1.0f);      // Scale down (NEW)
//        gl.glRotatef(angleCube, 0.0f, 1.0f, 0.0f); // rotate about the axis (1,1,1) (NEW)
//        cube.draw(gl);                      // Draw the cube (NEW)
        gl.glLoadIdentity();                // Reset the model-view matrix
        gl.glTranslatef(0.0f, 0.0f, -6.0f); // Translate right and into the screen
        gl.glScalef(2.0f, 2.0f, 2.0f);
        gl.glRotatef(angleCube, 0.0f, 1.0f, 0.0f); // rotate about the axis (1,1,1) (NEW)
        cube.draw(gl);
        // Update the rotational angle after each refresh

        if(angleCube < MIN_ANGLE || angleCube > MAX_ANGLE){
            speedCube = speedCube*(-1.0f);
        }

        angleCube += speedCube;         // (NEW)
    }


}