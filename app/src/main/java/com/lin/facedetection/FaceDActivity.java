package com.lin.facedetection;

/*
 * FaceDActivity
 * 
 * [AUTHOR]: zixin.lin
 * [SDK   ]: Android SDK 4.0 and up
 * "Face Detection with Android APIs"
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import java.util.List;

public class FaceDActivity extends Activity {
    private MyImageView mIV;
    private Bitmap mFaceBitmap;
    private int mFaceWidth = 200;
    private int mFaceHeight = 200;
    private static final int MAX_FACES = 10;
    private static String TAG = "TutorialOnFaceDetect";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIV = new MyImageView(this);
        setContentView(mIV, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // load the photo
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.face5);
        Matrix matrix = new Matrix();
        matrix.setScale(800f/b.getWidth(),800f/b.getWidth());
        b = Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(),matrix,false);
        mFaceBitmap = b.copy(Bitmap.Config.RGB_565, true);
        b.recycle();

        mFaceWidth = mFaceBitmap.getWidth();
        mFaceHeight = mFaceBitmap.getHeight();
        mIV.setImageBitmap(mFaceBitmap);

        // perform face detection and set the feature points
        setFace();

        mIV.invalidate();
        mIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startActivity(new Intent(FaceDActivity.this,MainActivity.class));
            }
        });
    }

    public void setFace() {
        FaceDetector fd;
        FaceDetector.Face [] faces = new FaceDetector.Face[MAX_FACES];
        PointF midpoint = new PointF();
        int [] fpx = null;
        int [] fpy = null;
        int [] length = null;
        int count = 0;

        try {

            fd = new FaceDetector(mFaceWidth, mFaceHeight, MAX_FACES);
            count = fd.findFaces(mFaceBitmap, faces);


        } catch (Exception e) {
            Log.e(TAG, "setFace(): " + e.toString());
            return;
        }
        for(int i=0;i<count;i++){
            Log.d("FaceDActivity","x:"+faces[i].pose(FaceDetector.Face.EULER_X)+" y:"+faces[i].pose(FaceDetector.Face.EULER_Y)+" z:"+faces[i].pose(FaceDetector.Face.EULER_Z));
        }


        // check if we detect any faces
        if (count > 0) {
            fpx = new int[count];
            fpy = new int[count];
            length = new int[count];
            for (int i = 0; i < count; i++) {
                try {
                    faces[i].getMidPoint(midpoint);

                    length[i] = (int) faces[i].eyesDistance();
                    fpx[i] = (int)midpoint.x;
                    fpy[i] = (int)midpoint.y;
                } catch (Exception e) {
                    Log.e(TAG, "setFace(): face " + i + ": " + e.toString());
                }
            }
        }

        mIV.setDisplayPoints(fpx, fpy, count, 0,length);
    }
}