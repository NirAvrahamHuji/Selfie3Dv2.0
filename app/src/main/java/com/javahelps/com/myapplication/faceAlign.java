package com.javahelps.com.myapplication;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;

public class faceAlign {

    private Mat srcImg;
    private Rect srcRect;
    private Size srcNoseShift;



    public faceAlign(Mat srcImg, Rect srcRect, Size srcNoseShift) {
        this.srcImg = srcImg;
        this.srcRect = srcRect;
        this.srcNoseShift = srcNoseShift;
    }

    public Mat processImage() {
        Mat dstImg = new Mat(Settings.ORIG_WIDTH_SIZE, Settings.ORIG_HEIGHT_SIZE, Settings.IMAGE_CVTYPE);

        return dstImg;
    }
}
