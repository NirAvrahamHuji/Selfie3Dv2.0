package com.javahelps.com.myapplication;

import org.opencv.core.Size;
import static org.opencv.core.CvType.CV_8UC1;

class Settings {

    static final int ENV_SIZE = 1;
    static final int IMAGE_CVTYPE = CV_8UC1;
    static final int WINDOW_SIZE = 128;
    static final int BLOCK_SIZE = 128;
    static final int CELL_SIZE = 128;
    static final int PADDING_SIZE = 0;


    static final int WIDTH_SIZE = 256;
    static final int HEIGHT_SIZE = 256;
    static final int ORIG_HEIGHT_SIZE = 938;
    static final int ORIG_WIDTH_SIZE = 938;
    static final float PATCH_SIZE_FLOAT = 32f;
    static final int PATCH_SIZE = (int) PATCH_SIZE_FLOAT;
    static final Size IMAGE_SIZE = new Size(WIDTH_SIZE, HEIGHT_SIZE);
    // TODO: when we retrieve the overlap return to < instead of <=
    static final int STEP_OVERLAP = 1;
    static final int OVERLAP_SIZE = PATCH_SIZE / STEP_OVERLAP;
    static final int QUERIES  = 64;

    static final int DESCRIPTOR_WANTED_SIZE = 128;

    static final int K_NEAREST = 1;
}
