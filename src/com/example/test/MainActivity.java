package com.example.test;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private TextView tv;
	private Button btn;
	private ImageView imageView;
	private Bitmap bitmap;
	private static final String  TAG = "Sample::Feature Detection::Activity";

	// callback for handling the connection status
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				try {
					Log.i(TAG, "OpenCV loaded successfully");
					// Not load , Oops
					System.loadLibrary("native-util");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			default:
				super.onManagerConnected(status);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.txt_view);
		btn = (Button) findViewById(R.id.btn_gray);
		imageView = (ImageView) findViewById(R.id.image_view);
		
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wheat);
		
		imageView.setImageBitmap(bitmap);
		
		btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		tv.setText(NativeUtil.stringFromJNI());
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Mat mat = new Mat(height, width, CvType.CV_8U, new Scalar(4));
		Utils.bitmapToMat(bitmap, mat);
		
		// do sth
		//Converts an image from one color space to another.
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY,4);
		
		//Then convert the processed Mat to Bitmap
		Bitmap resultBitmap = Bitmap.createBitmap(mat.cols(),  mat.rows(),Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(mat, resultBitmap);

		imageView.setImageBitmap(resultBitmap);

	}

	public void onResume() {
		super.onResume();
		// Loads and initializes OpenCV library using OpenCV Engine service.
		// You can choose it.
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this,
				mLoaderCallback);
	}
	/*
	 * this is used to load the 'hello-jni' library on application startup.
	 * 
	 * static { System.loadLibrary("native-util"); }
	 */
}
