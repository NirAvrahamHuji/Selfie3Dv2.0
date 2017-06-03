package com.javahelps.com.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


class VertexJack {
    private static final float X_CHANGE = 0.5f;
    private static final float Y_CHANGE = 0.67f;
    private static final float Z_CHANGE = 0.5f;
    private static final int Z_DEPTH = 255;
    private Context context;
    private Mat depthhImg;
    private Bitmap imgOrg;
    private Bitmap imgDepth;
    private Size s;
    float[][] colors;

    VertexJack(Context context, Bitmap depthImage){
        this.context = context;
        this.imgDepth = depthImage;
        this.depthhImg = getImgGrayScale();

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

        this.colors = new float[25000][4];
        float[] vertices = new float[300000];
        for(int i=0; i < 300000; i+=12){
            int color =imgOrg.getPixel(col,row);
//            Log.e(TAG,":color-"+color);
            this.colors[i/12][0] = (float)Color.red(color)/255;
            this.colors[i/12][1] = (float)Color.green(color)/255;
            this.colors[i/12][2] = (float)Color.blue(color)/255;
            this.colors[i/12][3] = 1.0f;

            if(depthhImg.get(row,col)[0] == 0){
                this.colors[i/12][0] = 0.0f;
                this.colors[i/12][1] = 0.0f;
                this.colors[i/12][2] = 0.0f;
                this.colors[i/12][3] = 1.0f;
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
        return vertices;
    }

    private void assignDotsValue(int row, int col, float[] vertices, int i) {
        vertices[i] = ((float)col/300)- X_CHANGE;
        vertices[i+1] = ((float)-row/403)+ Y_CHANGE;
        vertices[i+2] = ((float) depthhImg.get(row,col)[0]/ Z_DEPTH)- Z_CHANGE;
    }


}