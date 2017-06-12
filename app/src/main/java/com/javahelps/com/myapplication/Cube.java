package com.javahelps.com.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

class Cube {
    private FloatBuffer vertexBuffer;  // Buffer for vertex-array
    private int numFaces;

    private float[] vertices;
    private float[][] colors;

    //    float[][] colors;
    // Constructor - Set up the buffers
    Cube(Context context, Bitmap depthImage, Bitmap orgImage) {
        // Setup vertex-array buffer. Vertices in float. An float has 4 bytes
        VertexJack verJ = new VertexJack(context, depthImage);
        numFaces = VertexJack.COLORS_ARRAY_SIZE;
        orgImage = Bitmap.createScaledBitmap(orgImage, 300, 403, true);
        verJ.getRgb(orgImage);
        this.vertices =verJ.getVertices();
        this.colors = verJ.colors;
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder()); // Use native byte order
        vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
        vertexBuffer.put(this.vertices);         // Copy data into buffer
        vertexBuffer.position(0);           // Rewind
    }



    // Draw the shape
    void draw(GL10 gl) {
        gl.glFrontFace(GL10.GL_CCW);    // Front face in counter-clockwise orientation
        gl.glEnable(GL10.GL_CULL_FACE); // Enable cull face
        gl.glCullFace(GL10.GL_BACK);    // Cull the back face (don't display)
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        // Render all the faces
        for (int face = 0; face < numFaces; face++) {
            // Set the color for each of the faces

            gl.glColor4f(this.colors[face][0], this.colors[face][1], this.colors[face][2], 1.0f);
            // Draw the primitive from the vertex-array directly
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, face*4, 4);
        }
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
    }
}