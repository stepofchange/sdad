package org.opencv.samples.facedetect;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LockScreen extends AppCompatActivity  implements CameraBridgeViewBase.CvCameraViewListener2, OnClickListener {

	private static final String    TAG                 = "OCVSample::Activity";

    private Mat                    mRgba;
    private Mat                    mGray;
    
    private Button AddPersonBtn;
    
    private Mat mFaceMatrix;
    boolean recognize = false;
    
    int predictedLabel = -1;
    int label;
    double confidence;
  
    private File                   mCascadeFile;
    
    private DetectionBasedTracker  mNativeDetector;

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;
    
    private JavaCameraView mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("detection_based_tracker");

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1){
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
       getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.lockscreen);
        if ((getIntent() != null) && getIntent().hasExtra("kill")
				&& (getIntent().getExtras().getInt("kill") == 1)) {
			finish();
		}
        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.testview);
        mOpenCvCameraView.setCameraIndex(1);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
        AddPersonBtn = (Button)findViewById(R.id.testrecognize);
        AddPersonBtn.setOnClickListener(this);
        
        try {
			
			startService(new Intent(this, MyService.class));
			
			PhoneStateListener phoneStateListener = new PhoneStateListener();
			
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
    }    
	
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }
    
  

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);

        }

        MatOfRect faces = new MatOfRect();

        if (mNativeDetector != null){
            mNativeDetector.detect(mGray, faces);
           
            	Rect[] facesArray = faces.toArray();
            	for (int i = 0; i < facesArray.length; i++){
            		Rect r = facesArray[i];
            		Imgproc.rectangle(mGray, r.tl(), r.br(), new Scalar(255, 0, 255, 0), 3);
            		Imgproc.rectangle(mRgba, r.tl(), r.br(), new Scalar(255, 0, 255, 0), 3);
            		Imgproc.putText(mRgba, "Unknow", r.tl(), 3, 3, new Scalar(255, 0, 0, 255), 2);
            		Log.i("fsdhfsjdhfklsdf", "Negative " + String.valueOf(predictedLabel));
            
        }
        }

        return mRgba;
    }

    
	@Override
	public void onClick(View v) {

		 if (mAbsoluteFaceSize == 0) {
	            int height = mGray.rows();
	            if (Math.round(height * mRelativeFaceSize) > 0) {
	                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
	            }
	            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
	        }
		 
		 MatOfRect faces = new MatOfRect();

		 if (mNativeDetector != null){
	            mNativeDetector.detect(mGray, faces);
	        
	        }

		Rect[] facesArray = faces.toArray();
		
		for (int i = 0; i < facesArray.length; i++){
			Rect r = facesArray[i];
			mFaceMatrix = mRgba.submat(r.y, r.y + r.height, r.x, r.x + r.width);
		}
		
		Mat resizeimage = new Mat();
		Size sz = new Size(200,200);
		Imgproc.resize( mFaceMatrix, resizeimage, sz );
		
		
		Rect roi = new Rect(resizeimage.width()/100*20, resizeimage.height()/100*20, resizeimage.width()/100*60, resizeimage.height()/100*60);

		Mat warped = new Mat(resizeimage, roi);
		
		Imgproc.cvtColor(warped, warped, Imgproc.COLOR_RGB2GRAY, 1);
		Imgproc.equalizeHist(warped, warped);
		
		
		Mat filtered = new Mat(warped.size(), CvType.CV_8U);
        Imgproc.bilateralFilter(warped, filtered, 0, 20.0, 2.0);

        Mat mask = new Mat(filtered.size(), CvType.CV_8U, new Scalar(255));
        
        Mat dstImg = new Mat(filtered.size(), CvType.CV_8U, new Scalar(128));
        
        filtered.copyTo(dstImg, mask);
		
		Bitmap bitmap = Bitmap.createBitmap(dstImg.cols(), dstImg.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dstImg, bitmap);

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images/recognize");    
        myDir.mkdirs();
        String fname = "test.jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete (); 
        try {
               FileOutputStream out = new FileOutputStream(file);
               bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
               out.flush();
               out.close();
               Log.i("qqq", "successful image");

        } catch (Exception e) {
               e.printStackTrace();
               Log.i("qqq", "dont image");
        }
        
		
		String trainingDir = root + "/saved_images";

		opencv_core.Mat testImage = imread(root + "/saved_images/recognize/test.jpg", CV_LOAD_IMAGE_GRAYSCALE);


        File fileTraining = new File(trainingDir);

        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
            }
        };

        File[] imageFiles = fileTraining.listFiles(imgFilter);

        MatVector images = new MatVector(imageFiles.length);

       opencv_core.Mat labels = new opencv_core.Mat(imageFiles.length, 1, CV_32SC1);
  
		IntBuffer labelsBuf = labels.getIntBuffer();

        int counter = 0;

        for (File image : imageFiles) {
        	opencv_core.Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

            label = Integer.parseInt(image.getName().split("\\-")[0]);

            images.put(counter, img);

            labelsBuf.put(counter, label);

            counter++;
        }

      
       
        FaceRecognizer faceRecognizer = createLBPHFaceRecognizer(1, 8, 8, 8, 100);
        

        faceRecognizer.train(images, labels);
     
    	
        
        int[] plabel = new int[1];
        double[] pconfidence = new double[1];
        
        	faceRecognizer.predict(testImage, plabel, pconfidence);
        	
        	predictedLabel = plabel[0];
        	confidence = pconfidence[0];
        	
        if(predictedLabel>-1){
        	Intent i = new Intent(Intent.ACTION_MAIN);
        	i.addCategory(Intent.CATEGORY_HOME);
        	startActivity(i);
        	//finish();
        	predictedLabel = -1;
        }
	}
	

	
	
}
