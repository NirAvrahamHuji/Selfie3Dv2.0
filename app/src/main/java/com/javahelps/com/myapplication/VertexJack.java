package com.javahelps.com.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.nfc.Tag;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.InputStream;
import java.net.ContentHandler;


class VertexJack {
    public static final int COLORS_ARRAY_SIZE = 28000;
    public static final int VERTICES_SIZE = COLORS_ARRAY_SIZE * 12;
    private Context context;
    private Mat imgMat;
    private Bitmap imgOrg;
    private Bitmap imgDepth;
    private Size s;
    float[][] colors;

    VertexJack(Context context, Bitmap depthImage){
        this.context = context;
        this.imgDepth = depthImage;
        this.imgMat = getImgGrayScale();

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
        final String TAG = "VertexJack";
        int row = 0;
        int col = 0;

        this.colors = new float[COLORS_ARRAY_SIZE][4];
        float[] vertices = new float[VERTICES_SIZE];
        for(int i=0; i < VERTICES_SIZE; i+=12){
            int color =imgOrg.getPixel(col,row);
//            Log.e(TAG,":color-"+color);
            this.colors[i/12][0] = (float)Color.red(color)/255;
            this.colors[i/12][1] = (float)Color.green(color)/255;
            this.colors[i/12][2] = (float)Color.blue(color)/255;
            this.colors[i/12][3] = 1.0f;

            if(imgMat.get(row,col)[0] == 0){
                this.colors[i/12][0] = 0.0f;
                this.colors[i/12][1] = 0.0f;
                this.colors[i/12][2] = 0.0f;
                this.colors[i/12][3] = 1.0f;
            }
            row+=2;
            vertices[i] = ((float)col/300)-0.5f;
            vertices[i+1] = ((float)-row/300)+0.67f;
            vertices[i+2] = ((float)imgMat.get(row,col)[0]/255)-0.5f;
            col+=2;
            vertices[i+3] = ((float)col/300)-0.5f;
            vertices[i+4] = ((float)-row/300)+0.67f;
            vertices[i+5] = ((float)imgMat.get(row,col)[0]/255)-0.5f;
            row-=2;
            col-=2;
            vertices[i+6] = ((float)col/300)-0.5f;
            vertices[i+7] = ((float)-row/300)+0.67f;
            vertices[i+8] = ((float)imgMat.get(row,col)[0]/255)-0.5f;
            col+=2;

            vertices[i+9] = ((float)col/300)-0.5f;
            vertices[i+10] = ((float)-row/300)+0.67f;
            vertices[i+11] = ((float)imgMat.get(row,col)[0]/255)-0.5f;

            if(col>=298){
                col=0;
                row+=2;
            }

        }
        return vertices;
    }




}