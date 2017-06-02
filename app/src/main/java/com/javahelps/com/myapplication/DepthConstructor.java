package com.javahelps.com.myapplication;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;


class DepthConstructor {

    private ArrayList<DepthPatch> _patches;

    DepthConstructor(ArrayList patches) {
        _patches = patches;
    }

    Mat Construct() {
        Mat depth = Mat.zeros(Settings.IMAGE_SIZE, Settings.IMAGE_CVTYPE);
        for (DepthPatch dp : _patches) {
            if ((dp.getCol() % Settings.PATCH_SIZE != 0) || (dp.getRow() % Settings.PATCH_SIZE != 0)){
                continue;
            }
            dp.getDepthPatch().copyTo(
                    depth
                            .colRange(dp.getCol(), (dp.getCol() + Settings.PATCH_SIZE))
                            .rowRange(dp.getRow(), (dp.getRow() + Settings.PATCH_SIZE))
            );
        }
        return depth;
    }

    public Mat newConstruct() {

        Mat depth = Mat.zeros(Settings.IMAGE_SIZE, Settings.IMAGE_CVTYPE);
        Mat denom = Mat.zeros(Settings.IMAGE_SIZE, Settings.IMAGE_CVTYPE);

        for (DepthPatch dp : _patches) {
            Mat sMat = depth.colRange(dp.getCol(), (dp.getCol() + Settings.PATCH_SIZE))
                    .rowRange(dp.getRow(), (dp.getRow() + Settings.PATCH_SIZE));
            Mat sMatDenom = denom.colRange(dp.getCol(), (dp.getCol() + Settings.PATCH_SIZE))
                    .rowRange(dp.getRow(), (dp.getRow() + Settings.PATCH_SIZE));

            Core.add(sMat, dp.getDepthPatch(), sMat);

            Core.add(sMatDenom, new Scalar(1), sMatDenom);
        }

        Core.divide(depth, denom, depth);

        return depth;
    }

}