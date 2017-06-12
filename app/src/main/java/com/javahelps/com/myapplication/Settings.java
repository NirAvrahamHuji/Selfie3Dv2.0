package com.javahelps.com.myapplication;

import org.opencv.core.Size;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC4;

class Settings {

    static final int ENV_SIZE = 0;
    static final int IMAGE_CVTYPE = CV_8UC1;

    static final int IMAGE_CVTYPE_RGB = CV_8UC4;

    // the size we resize to input image to
    static final int WIDTH_SIZE = 128;
    static final int HEIGHT_SIZE = 128;

    // db images original size
    static final int ORIG_HEIGHT_SIZE = 938;
    static final int ORIG_WIDTH_SIZE = 938;

    static final float PATCH_SIZE_FLOAT = 32f;
    static final int PATCH_SIZE = (int) PATCH_SIZE_FLOAT;

    static int CROP_WIDTH;
    static int CROP_HEIGHT;

    // the input image original sizes
    static int INPUT_IMG_WIDTH;
    static int INPUT_IMG_HEIGHT;

    // the face original width and height
    static float FACE_INPUT_IMG_WIDTH;
    static float FACE_INPUT_IMG_HEIGHT;

    static final Size IMAGE_SIZE = new Size(WIDTH_SIZE, HEIGHT_SIZE);
    // TODO: when we retrieve the overlap return to < instead of <=
    private static final int STEP_OVERLAP = 1;
    static final int OVERLAP_SIZE = PATCH_SIZE / STEP_OVERLAP;

    static final int QUERIES  = 64;
    static final int DESCRIPTOR_WANTED_SIZE = 128;
    static final int K_NEAREST = 1;

    static int X_NOSE = 0;
    static int Y_NOSE = 0;
    static float SCALE_X = 0.0f;
    static float SCALE_Y = 0.0f;

    // Face Align

    static final double sideOffset  = 0;
    static final double upOffset  = 0;
    static final double downOffset  = 0.1;

    // the size of the detected face, (width, height)
    static final Size trgtRect = new Size(470, 600);
    // the nose position in the whole image size (938, 938),
    // (x, y)
    static final Size trgtNoseShift = new Size(460, 600);

    static void setNosePosition(float x_nose,float y_nose){
        X_NOSE = (int) x_nose;
        Y_NOSE = (int) y_nose;
    }
}
