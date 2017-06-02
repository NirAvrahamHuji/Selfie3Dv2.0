package com.javahelps.com.myapplication;


import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

class inputHandler {


    HashMap<Integer, HashMap<Integer, Mat>> splitToPatches(Mat imgMat) {
        // crate a matrix for the SIFT descriptors
        Mat forSIFTim = new Mat();

        // create the required size
        Size sz = new Size(Settings.WIDTH_SIZE, Settings.HEIGHT_SIZE);

        // resize and convert the colors of the img
        Imgproc.resize(imgMat, imgMat, sz);
        Imgproc.cvtColor(imgMat, forSIFTim, Imgproc.COLOR_RGB2GRAY);

        // create a hash map the contain the computed descriptors. the hashmap is col -> hashmap (row -> patch_descriptor)
        HashMap<Integer, HashMap<Integer, Mat>> img_descriptors = new HashMap<>();

        // run on the img and compute the SIFT descriptor for each patch
        // TODO: when overlap return to < instead of <=
        for (int col = 0; col <= (forSIFTim.cols() - Settings.OVERLAP_SIZE); col += Settings.OVERLAP_SIZE) {
            // create the inner hash map
            HashMap<Integer, Mat> descriptors_inner_map = new HashMap<>();

            // TODO: when overlap return to < instead of <=
            for (int row = 0; row <= (forSIFTim.rows() - Settings.OVERLAP_SIZE); row += Settings.OVERLAP_SIZE) {
                // take the whole mat as the patch and just choose
                Mat patch = forSIFTim;

                // a vector to contain the computed descriptor
                Mat patch_descriptor = calcDescriptor(col, row, patch);

                //insert the descriptor to the hashmap
                descriptors_inner_map.put(row, patch_descriptor);
            }

            img_descriptors.put(col, descriptors_inner_map);
        }

        return img_descriptors;
    }

    private Mat calcDescriptor(int col, int row, Mat patch) {
        Mat patch_descriptor = new Mat();

        // create the sift detector and extractor
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);

        // create a key point for the middle of the patch with environment in the size of the patch
        KeyPoint kp = new KeyPoint(col + (Settings.PATCH_SIZE_FLOAT / 2), row + (Settings.PATCH_SIZE_FLOAT / 2), Settings.PATCH_SIZE_FLOAT);
        MatOfKeyPoint mat_kps = new MatOfKeyPoint(kp);

        // compute the descriptor of our key point
        extractor.compute(patch, mat_kps, patch_descriptor);
        Log.i(TAG, "Computed");

        if (patch_descriptor.cols() != Settings.DESCRIPTOR_WANTED_SIZE) {

            Log.i("Compute Descriptor", String.format("patch_descriptor size is %d", patch_descriptor.cols()));
        }

        return patch_descriptor;
    }
}
