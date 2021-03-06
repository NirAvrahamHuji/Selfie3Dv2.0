package com.javahelps.com.myapplication;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.*;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

class Utils2D {
    private static final String TAG = "Utils2D";

    static Bitmap mat2bmp(Mat m) {
        Bitmap bmp = null;
        Mat tmp = new Mat (m.rows(), m.cols(), Settings.IMAGE_CVTYPE, new Scalar(4));

        try {
            Imgproc.cvtColor(m, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
            bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
            org.opencv.android.Utils.matToBitmap(tmp, bmp);
        }
        catch (CvException e){
            Log.e(TAG,e.getMessage());
        }

        return bmp;
    }

    static Bitmap mat2bmpRGB(Mat m) {
        Bitmap bmp = null;

        try {
            bmp = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(m, bmp);
        }
        catch (CvException e){
            Log.e(TAG,e.getMessage());
        }

        return bmp;
    }
}
