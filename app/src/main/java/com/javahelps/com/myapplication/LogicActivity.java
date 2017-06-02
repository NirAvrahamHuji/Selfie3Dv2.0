package com.javahelps.com.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.List;


import static org.opencv.core.Core.add;
import static org.opencv.core.Core.multiply;

public class LogicActivity extends AppCompatActivity {

    public static final Double MAX_FLOAT_NUM = Double.POSITIVE_INFINITY;
    private ImageView imgView;

    private inputHandler inputHandler;
    private Mat imgMat;
    DatabaseAccess databaseAccess;
    ArrayList<DepthPatch> depth_patches;
    Bitmap depthBmp;
    Bitmap orgBmp;
    int i = 0;
    Imgproc ip;
    Mat depth;

    //A ProgressDialog object
    private ProgressDialog progressDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        // Connect to image view.

        //Open image.

        byte[] bytes = getIntent().getByteArrayExtra("Face");
        orgBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        //Initialize a LoadViewTask object and call the execute() method
        new LoadViewTask().execute();



    }

    //To use the AsyncTask, it must be subclassed
    private class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {
        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            //Create a new progress dialog
            progressDialog = new ProgressDialog(LogicActivity.this);
            //Set the progress dialog to display a horizontal progress bar

            //Set the dialog message to 'Loading application View, please wait...'
            progressDialog.setMessage("Creating Depth map...");
            //This dialog can't be canceled by pressing the back key
            progressDialog.setCancelable(false);
            //This dialog isn't indeterminate
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //Display the progress dialog
            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params)
        {

            //Get the current thread's token
            synchronized (this) {
//        InputStream stream = getResources().openRawResource( R.raw.face );
//        orgBmp = BitmapFactory.decodeStream(stream);

                imgMat = getMatFromBitmap(orgBmp);

                inputHandler = new inputHandler();

                // split the input img into patches
                HashMap<Integer, HashMap<Integer, Mat>> img_descriptors = inputHandler.splitToPatches(imgMat);

                depth_patches = processDescriptors(img_descriptors);

                depth = createDepthMap(depth_patches);

                depthBmp = Utils2D.mat2bmp(depth);


            }

        return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            //set the current progress of the progress dialog
            progressDialog.setProgress(values[0]);
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            //close the progress dialog
            progressDialog.dismiss();
            //initialize the View
            setContentView(R.layout.activity_logic);
            imgView = (ImageView) findViewById(R.id.faceImage);
            imgView.setImageBitmap(depthBmp);
            databaseAccess.close();
        }
    }

    public void optimize_output(View view) {
        ip = new Imgproc();
//        depth.convertTo(depth,CvType.CV_8UC1);
        ip.pyrMeanShiftFiltering(depth, depth, 4, 4);
        Bitmap depthbmp = Utils2D.mat2bmp(depth);
        imgView.setImageBitmap(depthbmp);
    }

    public void showDepthPatch(View view){
        DepthPatch d = depth_patches.get(i);
        Bitmap dpbmp = Utils2D.mat2bmp(d.getDepthPatch());
        imgView.setImageBitmap(dpbmp);
        i++;
    }

    private Mat createDepthMap(ArrayList<DepthPatch> depth_patches) {
//        Collections.sort(depth_patches, getCompByName());
        DepthConstructor dc = new DepthConstructor(depth_patches);
        return dc.Construct();
    }

    private ArrayList<DepthPatch> processDescriptors(HashMap<Integer, HashMap<Integer, Mat>> img_descriptors) {
        // create an array list to store all the depth patches
        ArrayList<DepthPatch> dps = new ArrayList<>();

        int i = 0;
        //run on all the patches
        for (Map.Entry<Integer, HashMap<Integer, Mat>> col2hashmap : img_descriptors.entrySet()) {
            for (Map.Entry<Integer, Mat> row2descriptor : col2hashmap.getValue().entrySet()) {
                Mat input_descriptor = row2descriptor.getValue();
                Integer input_col = col2hashmap.getKey();
                Integer input_row = row2descriptor.getKey();

                // send the col, row to get all the descriptors in the environment
                List<Descriptor> env_descs = getPatchEnvDescs(input_col, input_row);

                double min_dist = MAX_FLOAT_NUM;
                Descriptor min_desc = new Descriptor();

                Map<Descriptor, Double> k_nearest = new HashMap<>();

                // run on all the descriptor and save the one with the smallest distance
                for (Descriptor db_desc:env_descs) {
                    double dis_res = db_desc.distanceFrom(input_descriptor);

                    k_nearest.put(db_desc, dis_res);
                }

                // sorting all results
                k_nearest = sortByValue(k_nearest);

                // create an empty matrix to contain the k nearest average patch
                Mat avg = Mat.zeros(Settings.PATCH_SIZE, Settings.PATCH_SIZE, CvType.CV_32F);

                int k_count = 0;

                for(Map.Entry<Descriptor, Double> desc2float : k_nearest.entrySet()){
                    Descriptor curr_desc = desc2float.getKey();

                    // get the patch depth map
                    DepthPatch dp = getDepthPatch(curr_desc.getID(), curr_desc.getCol(), curr_desc.getRow());

                    Mat float_mat = Mat.zeros(Settings.PATCH_SIZE, Settings.PATCH_SIZE, CvType.CV_32F);
                    dp.getDepthPatch().convertTo(float_mat, CvType.CV_32F);

                    add(float_mat,avg, avg);

                    k_count++;
                    if (k_count >= Settings.K_NEAREST) break;
                }

                // create the 1/k num to divide the sum matrix with
                Scalar s = new Scalar((double)1/Settings.K_NEAREST);
                multiply(avg,s, avg);
                avg.convertTo(avg,Settings.IMAGE_CVTYPE);

                // create the final depth patch
                DepthPatch res_dp = new DepthPatch(-1, input_col, input_row, avg);
                dps.add(res_dp);
                Log.i("Queries", String.format("done processing %d queries", ++i));
            }
        }
        Log.i("Queries", "done processing all the queries");

        return dps;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ){
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

    private DepthPatch getDepthPatch(int id, int col, int row) {
        String query = String.format("select * from depth_patches where id == %d and col == %d and row == %d;", id, col, row);
        DepthPatch query_res = databaseAccess.exeDepthPatchesQuery(query);

        return query_res;
    }

    private List<Descriptor> getPatchEnvDescs(Integer col, Integer row) {
        // create an array to store all the relevant descriptors
        Integer upper_col = col + Settings.ENV_SIZE + Settings.PATCH_SIZE;
        Integer lower_col = col - Settings.ENV_SIZE;
        Integer upper_row = row + Settings.ENV_SIZE + Settings.PATCH_SIZE;
        Integer lower_row = row - Settings.ENV_SIZE;

        String query = String.format("select * from descriptors where col >= %d and col < %d and row >= %d and row < %d;", lower_col, upper_col, lower_row, upper_row);
        List<Descriptor> query_res = databaseAccess.exeDescriptorsQuery(query);

        return query_res;
    }

    private Mat getMatFromBitmap(Bitmap bmp){
        Mat sourceImage = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_16UC1);
        Utils.bitmapToMat(bmp, sourceImage);
        return sourceImage;
    }

    public void nextActivity(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        depthBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        intent.putExtra("bitmapbytes",bytes);

        ByteArrayOutputStream streamOrg = new ByteArrayOutputStream();
        orgBmp.compress(Bitmap.CompressFormat.JPEG, 100, streamOrg);
        byte[] bytesOrg = streamOrg.toByteArray();
        intent.putExtra("bitmapbytesOrg",bytesOrg);

        startActivity(intent);
    }


    public ArrayList<Float> MatOftoList(Mat input_mat) {
        ArrayList<Float> res_list = new ArrayList<>();
        for(int col =0; col < input_mat.cols(); col++) {
            for (int row = 0; row < input_mat.rows(); row++) {
                res_list.add((float) input_mat.get(row, col)[0]);
            }
        }

        return res_list;
    }
}
