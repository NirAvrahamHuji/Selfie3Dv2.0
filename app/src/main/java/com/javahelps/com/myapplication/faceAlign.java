package com.javahelps.com.myapplication;

import android.support.constraint.solver.widgets.Rectangle;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


class faceAlign {

    private Mat srcImg;
    private Rectangle srcRect;
    private Size srcNoseShift;


    faceAlign(Mat srcImg, Rectangle srcRect, Size srcNoseShift) {
        this.srcImg = srcImg;
        this.srcRect = srcRect;
        this.srcNoseShift = srcNoseShift;
    }

    public Mat processImage() {

        Mat dstImg = Mat.zeros(Settings.ORIG_WIDTH_SIZE, Settings.ORIG_HEIGHT_SIZE, Settings.IMAGE_CVTYPE_RGB);

        Double x1d = srcRect.x - srcRect.width * Settings.sideOffset;
        Double x2d = srcRect.x + srcRect.width * (1 + Settings.sideOffset);

        Double y1d = srcRect.y - srcRect.height * Settings.upOffset;
        Double y2d = srcRect.y + srcRect.height * (1 + Settings.downOffset);

        int x1 = x1d.intValue();
        int x2 = x2d.intValue();

        int y1 = y1d.intValue();
        int y2 = y2d.intValue();

        Mat resizedFace = new Mat(new Size(x2 - x1, y2 - y1), Settings.IMAGE_CVTYPE_RGB);

        Imgproc.resize(srcImg.colRange(x1, x2).rowRange(y1, y2), resizedFace, resizedFace.size());

        Double xNosed = Settings.trgtRect.width * (Settings.sideOffset + srcNoseShift.width / srcRect.width);
        Double yNosed = Settings.trgtRect.height * (Settings.sideOffset + srcNoseShift.height / srcRect.height);

        Double copyToPointxd = Settings.trgtNoseShift.width - xNosed.intValue();
        Double copyToPointyd = Settings.trgtNoseShift.height - yNosed.intValue();

        int copyToPointx1 = Math.max(copyToPointxd.intValue(), 0);
        int copyToPointy1 = Math.max(copyToPointyd.intValue(), 0);
        int copyToPointx2 = Math.min(copyToPointx1 + resizedFace.cols(), dstImg.cols());
        int copyToPointy2 = Math.min(copyToPointy1 + resizedFace.rows(), dstImg.rows());

        if (copyToPointx1 == 0) {
            copyToPointx2 = resizedFace.cols();
        }
        if (copyToPointy1 == 0) {
            copyToPointy2 = resizedFace.rows();
        }
        if (copyToPointx2 == dstImg.cols()) {
            copyToPointx1 = dstImg.cols() - resizedFace.cols();
        }
        if (copyToPointy2 == dstImg.rows()) {
            copyToPointy1 = dstImg.rows() - resizedFace.rows();
        }

        resizedFace.copyTo(dstImg.colRange(copyToPointx1, copyToPointx2).rowRange(copyToPointy1, copyToPointy2));

        return dstImg;
    }
}
