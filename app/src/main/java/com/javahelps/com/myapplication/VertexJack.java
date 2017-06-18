package com.javahelps.com.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


class VertexJack {

    private static final String TAG = "VertexJack";

    static final int COLORS_ARRAY_SIZE =29000;
    private static final int VERTICES_SIZE = COLORS_ARRAY_SIZE * 12;
    private static final float X_CHANGE = -1.51f;
    private static final float Y_CHANGE = 0.67f;
    private static final float Z_CHANGE = 0.5f;
    private static final int Z_DEPTH = 255;
    private Context context;
    private Mat depthImg;
    private Bitmap imgOrg;
    private Bitmap imgDepth;
    private Size s;
    float[][] colors;

    VertexJack(Context context, Bitmap depthImage){
        this.context = context;
        this.imgDepth = depthImage;
        this.depthImg = getImgGrayScale();

//        InputStream stream = context.getResources().openRawResource(R.raw.jack);

    }

    private Mat getImgGrayScale() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
//        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(),R.raw.jack, options);
        Bitmap bMap = imgDepth;
        bMap = Bitmap.createScaledBitmap(bMap, 300, 403, true);
        Mat sourceImage = new Mat(bMap.getWidth(), bMap.getHeight(), Settings.IMAGE_CVTYPE);
        Utils.bitmapToMat(bMap, sourceImage);
        this.s = sourceImage.size();
        Mat imgGrayScale = new Mat((int)s.height, (int)s.width, Settings.IMAGE_CVTYPE);
        Imgproc.cvtColor(sourceImage, imgGrayScale, Imgproc.COLOR_BGR2GRAY);
        return imgGrayScale;
    }
    void getRgb(Bitmap myImage){
        this.imgOrg = myImage;
    }

    float[] getVertices(){
        int row = 0;
        int col = 0;

        this.colors = new float[COLORS_ARRAY_SIZE][4];
        float[] vertices = new float[VERTICES_SIZE];
        for(int i=0; i < VERTICES_SIZE; i+=12){
            int color =imgOrg.getPixel(col,row);

            setColor(i, color, false);

            if(depthImg.get(row,col)[0] == 0){
                setColor(i, color, true);
            }
            row+=2;

            assignDotsValue(row, col, vertices, i);

            col+=2;

            assignDotsValue(row, col, vertices, i + 3);

            row-=2;
            col-=2;

            assignDotsValue(row, col, vertices, i + 6);

            col+=2;

            assignDotsValue(row, col, vertices, i + 9);

            if(col>=298){
                col=0;
                row+=2;
            }

        }

        Log.d(TAG, String.format("Dots: x: %f y: %f z: %f",x_avg / 300, y_avg / 403, z_avg));
        return vertices;
    }

    private void setColor(int i, int color, boolean isBlack) {
        this.colors[i/12][0] = (float) Color.red(color)/255;
        this.colors[i/12][1] = (float)Color.green(color)/255;
        this.colors[i/12][2] = (float)Color.blue(color)/255;
        this.colors[i/12][3] = 1.0f;
        if(isBlack){
            this.colors[i/12][0] = 1.0f;
            this.colors[i/12][1] = 1.0f;
            this.colors[i/12][2] = 1.0f;
            this.colors[i/12][3] = 1.0f;
        }
    }

    private float x_avg = 0;
    private float y_avg = 0;
    private float z_avg = 0;

    private void assignDotsValue(int row, int col, float[] vertices, int i) {
        vertices[i] = ((float)col - 150+ X_CHANGE)/300;
        vertices[i+1] = ((float) -row /403) + Y_CHANGE;
        vertices[i+2] = ((float) depthImg.get(row,col)[0]/ Z_DEPTH)- Z_CHANGE;
        x_avg += vertices[i];
        y_avg += vertices[i+1];
        z_avg += vertices[i+2];

    }


}