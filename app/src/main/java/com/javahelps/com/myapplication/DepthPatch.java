package com.javahelps.com.myapplication;

import org.opencv.core.Mat;

import java.util.Comparator;


class DepthPatch implements Comparator<DepthPatch>{

    //private variables
    private int _id;
    private int _col;
    private int _row;
    private Mat _dp;

    // Empty constructor
    DepthPatch(){}

    // constructor
    DepthPatch(int id, int col, int row, Mat dp){
        this._id = id;
        this._col = col;
        this._row = row;
        this._dp = dp;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting ID
    void setID(int id){
        this._id = id;
    }

    // getting col
    int getCol(){
        return this._col;
    }

    // setting col
    void setCol(int col){
        this._col = col;
    }

    int getRow(){
        return this._row;
    }

    // setting row
    void setRow(int row){
        this._row = row;
    }

    // getting depth patch
    Mat getDepthPatch(){
        return this._dp;
    }

    // setting depth patch
    void setDP(Mat dp){
        this._dp = dp;
    }

    @Override
    public int compare(DepthPatch o1, DepthPatch o2) {
        if (o1.getRow() < o2.getRow()) {
            return -1;
        }
        else if (o1.getRow() > o2.getRow()) {
            return 1;
        }
        else if (o1.getCol() < o2.getCol()) {
            return -1;
        }
        else if (o1.getCol() > o2.getCol()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
