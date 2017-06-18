package com.javahelps.com.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.*;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java");
        // the SIFT feature is available only in the non free package
        System.loadLibrary("nonfree");
    }

    public static final int NOSE_TYPE = 6;
    public static final int RESIZE_CAMERA_INPUT_FACTOR = 2;
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
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            mImageBitmap = BitmapFactory.decodeFile(picturePath);
        }

        //Get image from camera.
        if(requestCode == CameraImage.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            mImageBitmap = cam_img.setPic(mImageView);
            // resize the camera input so it won't crash the nextActivity process
            mImageBitmap = Bitmap.createScaledBitmap(mImageBitmap, mImageBitmap.getWidth() / RESIZE_CAMERA_INPUT_FACTOR, mImageBitmap.getHeight() / RESIZE_CAMERA_INPUT_FACTOR, true);

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

        // check if we choose the the crop button without choosing an input image
        if (mImageBitmap == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "No input image is given", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (!detector.isOperational()) {
            Log.w(TAG, "FaceDetector Failed");
        } else {
            Frame frame = new Frame.Builder().setBitmap(mImageBitmap).build();
            mFaces = detector.detect(frame);
            if(mFaces.size() != 0){
                Settings.INPUT_IMG_HEIGHT = mImageBitmap.getHeight();
                Settings.INPUT_IMG_WIDTH = mImageBitmap.getWidth();

                Rectangle rect = drawFaceBorder(mFaces);
                Log.d(TAG, String.format("x, y : %d %d \n width, height %d %d \n input width , height: %d %d", rect.x, rect.y, rect.width, rect.height, mImageBitmap.getWidth(), mImageBitmap.getHeight()));

                Mat face_align_input_mat = new Mat();
                Utils.bitmapToMat(mImageBitmap, face_align_input_mat);

                faceAlign face_align_obj = new faceAlign(face_align_input_mat, rect, new Size(Settings.X_NOSE, Settings.Y_NOSE));
                Mat aligned_input_mat = face_align_obj.processImage();
                mImageBitmap = Utils2D.mat2bmpRGB(aligned_input_mat);

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
        imageChosen = true;
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
                Log.d(TAG, String.format("NosePosition: %f %f ", nose_x, nose_y));
            }
        }

        Settings.FACE_INPUT_IMG_WIDTH = face.getWidth();
        Settings.FACE_INPUT_IMG_HEIGHT = face.getHeight();

        Settings.setNosePosition(nose_x - Math.max((int)face.getPosition().x, 0), nose_y - Math.max((int)face.getPosition().y, 0));

        rect.setBounds(Math.max((int)face.getPosition().x, 0), Math.max((int)face.getPosition().y, 0), (int)face.getWidth(), (int)face.getHeight());

        return rect;
    }
}


