package com.javahelps.com.myapplication;


import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


public class MatSerializer {

    private static final int WIDTH = Settings.PATCH_SIZE;
    private static final int HEIGHT = Settings.PATCH_SIZE;

    public static Mat string2Mat(String matStr, Boolean isDesc) {
        Mat res_dp;
        res_dp = isDesc ? new Mat( 1,Settings.DESCRIPTOR_WANTED_SIZE, CvType.CV_32F):new Mat(Settings.PATCH_SIZE, Settings.PATCH_SIZE, Settings.IMAGE_CVTYPE);

        String[] rows_str = matStr.split("\\n");

        for (int row = 0; row < rows_str.length; row++) {
            String[] floats_list_str = rows_str[row].split(",");


            for (int col = 0; col < floats_list_str.length; col++) {
                res_dp.put(row, col, Float.parseFloat(floats_list_str[col]));
            }
        }

        return res_dp;
    }

    public static Mat string2MatDescriptor(String matStr) {
        Mat res_dp = new Mat(Settings.PATCH_SIZE, Settings.PATCH_SIZE, Settings.IMAGE_CVTYPE);

        String[] rows_str = matStr.split("\\n");

        for (int row = 0; row < rows_str.length; row++) {
            String[] floats_list_str = rows_str[row].split(",");


            for (int col = 0; col < floats_list_str.length; col++) {
                res_dp.put(row, col, Float.parseFloat(floats_list_str[col]));
            }
        }

        return res_dp;
    }

    public static MatOfFloat string2MatOfFloat(String matStr) {
        MatOfFloat m = new MatOfFloat();

        String[] floats_list_str = matStr.replace("[", "").replace("]", "").split(",");

        List<Float> floats_list = new ArrayList<Float>();

        for (String aFloats_list_str : floats_list_str) {
            floats_list.add(Float.parseFloat(aFloats_list_str));
        }

        m.fromList(floats_list);

        return m;
    }


}