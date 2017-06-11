package com.javahelps.com.myapplication;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;

/**
 * Created by idan on 11/06/2017.
 */

public class faceAlign {

    private Mat srcImg;
    private Rect srcRect;
    private Size srcNoseShift;
    private Size trgtRect;
    private Size trgtNoseShift;


    public faceAlign(Mat srcImg, Rect srcRect, Size srcNoseShift, Size trgtRect, Size trgtNoseShift) {
        this.srcImg = srcImg;
        this.srcRect = srcRect;
        this.srcNoseShift = srcNoseShift;
        this.trgtRect = trgtRect;
        this.trgtNoseShift = trgtNoseShift;
    }
}
