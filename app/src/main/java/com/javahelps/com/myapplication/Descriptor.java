package com.javahelps.com.myapplication;

import org.opencv.core.Core;
import org.opencv.core.Mat;

class Descriptor {
    //private variables
    private int _id;
    private int _col;
    private int _row;
    private Mat _desc;

    // Empty constructor
    Descriptor(){}

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

    // getting descriptor
    public Mat getDescriptor(){
        return this._desc;
    }

    // setting descriptor
    void setDesc(Mat desc){
        this._desc = desc;
    }

    float distanceFrom(Mat other) {
        return (float) Core.norm(_desc, other);
    }

}
