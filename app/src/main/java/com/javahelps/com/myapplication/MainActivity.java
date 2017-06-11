package com.javahelps.com.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.provider.*;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final int NOSE_TYPE = 6;
    private static final int NORMALIZE_NOSE_X = 450;
    private static final int NORMALIZE_NOSE_Y = 620;


    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("nonfree");
    }

    public static final int INPUT_IMG = R.raw.face22;
    static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "MainActivity";

    ImageView mImageView;
    CameraImage cam_img;
    Bitmap mImageBitmap;
    ImageButton rotateButton;
    Boolean imageChosen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image);
        rotateButton = (ImageButton) findViewById(R.id.rotate);
        cam_img = new CameraImage(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        //Get the image from gallery.
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != intent) {
            Uri selectedImage = intent.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            mImageBitmap = BitmapFactory.decodeFile(picturePath);
        }
        //Get image from camera.
        if(requestCode == CameraImage.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            mImageBitmap = cam_img.setPic(mImageView);
        }else if(requestCode == CameraImage.REQUEST_TAKE_PHOTO && resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
        }
        imageChosen = true;
        rotateButton.setVisibility(View.VISIBLE);
        mImageView.setImageBitmap(mImageBitmap);
    }
    public void imageFromCamera(View view){
        //Open Camera and return to onActivityResult.
        cam_img.dispatchTakePictureIntent();
        rotateButton.setVisibility(View.VISIBLE);

    }

    public void imageFromGallery(View view){
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void faceDetection(View view){
        SparseArray<Face> mFaces;
        FaceDetector detector = new FaceDetector.Builder(this).setTrackingEnabled(true).setLandmarkType(FaceDetector.ALL_LANDMARKS).setMode(FaceDetector.ALL_LANDMARKS).build();
        if (!detector.isOperational()) {
            Log.i(TAG,"FaceDetector Failed");
        } else {
            Frame frame = new Frame.Builder().setBitmap(mImageBitmap).build();
            mFaces = detector.detect(frame);
            if(mFaces.size() != 0){
                Settings.INPUT_IMG_HEIGHT = mImageBitmap.getHeight();
                Settings.INPUT_IMG_WIDTH = mImageBitmap.getWidth();

                Rectangle rect = drawFaceBorder(mFaces);
                Log.d(TAG, String.format("x, y : %d %d \n width, height %d %d \n input width , height: %d %d", rect.x, rect.y, rect.width, rect.height, mImageBitmap.getWidth(), mImageBitmap.getHeight()));

                // moved to the align face functionality
//                mImageBitmap = Bitmap.createBitmap(mImageBitmap, rect.x, rect.y, Math.min(rect.width, mImageBitmap.getWidth() - 1), Math.min(rect.height, mImageBitmap.getHeight() - 1));
//                Settings.CROP_WIDTH = Math.min(rect.width, mImageBitmap.getWidth() - 1);
//                Settings.CROP_HEIGHT = Math.min(rect.height, mImageBitmap.getHeight() - 1);

                Mat face_align_input_mat = new Mat();
                Utils.bitmapToMat(mImageBitmap, face_align_input_mat);

                faceAlign face_align_obj = new faceAlign(face_align_input_mat, rect, new Size(Settings.X_NOSE, Settings.Y_NOSE));
                Mat aligned_input_mat = face_align_obj.processImage();

                Utils.matToBitmap(aligned_input_mat, mImageBitmap);

                mImageView.setImageBitmap(mImageBitmap);
                detector.release();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Couldn't find face", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }

    public void imageFromDataBase(View view){
        InputStream stream = getResources().openRawResource(INPUT_IMG);
        mImageBitmap = BitmapFactory.decodeStream(stream);
        rotateButton.setVisibility(View.VISIBLE);
        mImageView.setImageBitmap(mImageBitmap);
        imageChosen =true;
    }

    public void nextActivity(View view){
        if(!imageChosen){
            Toast.makeText(this, "Please Choose Picture", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, LogicActivity.class);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra("Face",byteArray);
        startActivity(intent);
    }

    public void rotateImage(View view){
        mImageBitmap = cam_img.rotateImage(mImageBitmap);
        mImageView.setImageBitmap(mImageBitmap);
    }

    private Rectangle drawFaceBorder(SparseArray<Face> mFaces) {
        float nose_x = 0;
        float nose_y = 0;

        Rectangle rect = new Rectangle();
        Face face = mFaces.valueAt(0);
        List<Landmark> landmarks = face.getLandmarks();
        for (Landmark landmark:landmarks) {
            int type = landmark.getType();
            if (type == NOSE_TYPE)
            {
                nose_x = landmark.getPosition().x;
                nose_y = landmark.getPosition().y;
                System.out.println(String.format("NosePosition: %f %f ", nose_x, nose_y));
            }
        }

        Settings.FACE_INPUT_IMG_WIDTH = face.getWidth();
        Settings.FACE_INPUT_IMG_HEIGHT = face.getHeight();

        Settings.setNosePosition(nose_x - Math.max((int)face.getPosition().x, 0), nose_y - Math.max((int)face.getPosition().y, 0));

        rect.setBounds(Math.max((int)face.getPosition().x, 0), Math.max((int)face.getPosition().y, 0), (int)face.getWidth(), (int)face.getHeight());

        // to take an environment - use this code
        //rect.setBounds(Math.max((int)face.getPosition().x - x_change, 0), Math.max((int)face.getPosition().y - y_change, 0),(int) (2 * face.getWidth()), newHeight);

        return rect;

    }

    public Mat alignImages(Mat input_img_mat){
        Settings.X_NOSE = (int) (Settings.X_NOSE / Settings.SCALE_X);
        Settings.Y_NOSE = (int) (Settings.Y_NOSE / Settings.SCALE_Y);
        int x_translate = Settings.X_NOSE  - NORMALIZE_NOSE_X;
        int y_translate = Settings.Y_NOSE  - NORMALIZE_NOSE_Y;

        Settings.X_NOSE += x_translate;
        Settings.Y_NOSE += y_translate;

        double[][] intArray = new double[][]{{1d,0d,(double)x_translate},{0d,1d,(double)y_translate}};
        Mat matObject = new Mat(2,3,CvType.CV_32F);
        for(int row=0;row<2;row++){
            for(int col=0;col<3;col++)
                matObject.put(row, col, intArray[row][col]);
        }

        Imgproc.warpAffine(input_img_mat,input_img_mat,matObject,new Size(Settings.ORIG_WIDTH_SIZE, Settings.ORIG_HEIGHT_SIZE));
//        input_img_mat.convertTo(input_img_mat, CvType.CV_8UC3);

        System.out.println("FINISHED");

        return input_img_mat;
    }

    private Mat getMatFromBitmap(Bitmap bmp){
        Mat sourceImage = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC3);
        Utils.bitmapToMat(bmp, sourceImage);
//        Imgproc.cvtColor(sourceImage, sourceImage,Imgproc.COLOR_RGB2GRAY);

        return sourceImage;
    }
}


