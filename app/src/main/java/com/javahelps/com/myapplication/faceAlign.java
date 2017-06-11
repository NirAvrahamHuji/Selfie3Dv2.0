package com.javahelps.com.myapplication;

import android.support.constraint.solver.widgets.Rectangle;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;

class faceAlign {

    private Mat srcImg;
    private Rectangle srcRect;
    private Size srcNoseShift;



    faceAlign(Mat srcImg, Rectangle srcRect, Size srcNoseShift) {
        this.srcImg = srcImg;
        this.srcRect = srcRect;
        this.srcNoseShift = srcNoseShift;
    }

    Mat processImage() {
        Mat dstImg = new Mat(Settings.ORIG_WIDTH_SIZE, Settings.ORIG_HEIGHT_SIZE, Settings.IMAGE_CVTYPE);

        return dstImg;
    }
}
