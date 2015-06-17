package com.example.test;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FeatureActivity extends Activity {
	private Button btn_feature_detect;
	private ImageView imageFeatureView;
	private Bitmap bitmap;
	private static final String TAG = "Feature Detection::Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feature);
		// Set background color
		getWindow().getDecorView().setBackgroundColor(Color.LTGRAY);

		btn_feature_detect = (Button) findViewById(R.id.btn_feature_detect);
		imageFeatureView = (ImageView) findViewById(R.id.image_feature_view);

		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wheat);

		imageFeatureView.setImageBitmap(bitmap);

		btn_feature_detect.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				Mat mRgba = new Mat(height, width, CvType.CV_8U, new Scalar(4));
				Mat mGray = new Mat(height, width, CvType.CV_8U, new Scalar(4));
				Mat output = new Mat();
				
				Utils.bitmapToMat(bitmap, mRgba);
				
				// do sth
				// Converts an image from one color space to another.
				Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGB2GRAY, 4);
				
				NativeUtil.detectFeatures(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr(), output.getNativeObjAddr());

				// Then convert the processed Mat to Bitmap
				Bitmap resultBitmap = Bitmap.createBitmap(output.cols(),
						output.rows(), Bitmap.Config.ARGB_8888);
				
				Utils.matToBitmap(output, resultBitmap);

				imageFeatureView.setImageBitmap(resultBitmap);
			}
		});
	}

}
