package com.javahelps.com.myapplication;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static com.javahelps.com.myapplication.Settings.trgtNoseShift;

/**
 * Created by idan on 11/06/2017.
 */

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

        Mat dstImg = Mat.zeros(Settings.ORIG_WIDTH_SIZE, Settings.ORIG_HEIGHT_SIZE, Settings.IMAGE_CVTYPE);

        Double x1d = srcRect.x - srcRect.width * Settings.sideOffset;
        Double x2d = srcRect.x + srcRect.width * (1 + Settings.sideOffset);

        Double y1d = srcRect.y - srcRect.height * Settings.upOffset;
        Double y2d = srcRect.y + srcRect.height * (1 + Settings.downOffset);

        int x1 = x1d.intValue();
        int x2 = x2d.intValue();

        int y1 = y1d.intValue();
        int y2 = y2d.intValue();

        Mat resizedFace = new Mat(new Size(x2 - x1, y2 - y1), Settings.IMAGE_CVTYPE);

        Imgproc.resize(srcImg.colRange(x1, x2).rowRange(y1, y2), resizedFace, new Size(0,0));

        Double xNosed = Settings.trgtRect.width * (Settings.sideOffset + srcNoseShift.width / srcRect.width);
        Double yNosed = Settings.trgtRect.width * (Settings.sideOffset + srcNoseShift.width / srcRect.width);

        int copyToPointx = Settings.trgtNoseShift.width - xNosed.intValue();
        int copyToPointy = Settings.trgtNoseShift.height - yNosed.intValue();

        resizedFace.copyTo(dstImg.colRange(copyToPointx + resizedFace.width()));

        return dstImg;
    }
}
