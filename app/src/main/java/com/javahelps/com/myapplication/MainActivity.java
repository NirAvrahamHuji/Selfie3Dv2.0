package com.javahelps.com.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("nonfree");
    }

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
        FaceDetector detector = new FaceDetector.Builder(this).setTrackingEnabled(true).setLandmarkType(FaceDetector.ALL_LANDMARKS).setMode(FaceDetector.ACCURATE_MODE).build();
        if (!detector.isOperational()) {
            Log.i(TAG,"FaceDetector Failed");
        } else {
            Frame frame = new Frame.Builder().setBitmap(mImageBitmap).build();
            mFaces = detector.detect(frame);
            if(mFaces.size() != 0){
                Rectangle rect = drawFaceBorder(mFaces);
                Log.d(TAG,""+rect.x+" "+rect.y+" " + (rect.width+rect.x)+  " " + (rect.height+rect.y) +" mBitMapWidth:" +mImageBitmap.getWidth()+ " mBitMapHeight:" + mImageBitmap.getHeight());
                mImageBitmap = Bitmap.createBitmap(mImageBitmap,rect.x, rect.y, Math.min(rect.width,mImageBitmap.getWidth()), Math.min(rect.height,mImageBitmap.getHeight()));
                mImageView.setImageBitmap(mImageBitmap);
                detector.release();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Couldn't find face", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }

    public void imageFromDataBase(View view){
        InputStream stream = getResources().openRawResource( R.raw.face8 );
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

        Rectangle rect = new Rectangle();
        Face face = mFaces.valueAt(0);
        rect.setBounds(Math.max((int)face.getPosition().x,0),Math.max((int)face.getPosition().y,0),(int)face.getWidth(),(int)face.getHeight());

        return rect;

    }
}


