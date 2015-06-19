package com.example.test;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class FeatureActivity extends Activity {
	private Button btn_feature_detect;
	private Button btn_feature_next;
	private ImageView imageFeatureView;
	private Bitmap bitmap;

	private static int RESULT_LOAD_IMG = 1;
	private String imgDecodableString;
	private static final String TAG = "Feature Detection::";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feature);
		// Set background color
		getWindow().getDecorView().setBackgroundColor(Color.LTGRAY);

		btn_feature_detect = (Button) findViewById(R.id.btn_feature_detect);
		btn_feature_next = (Button) findViewById(R.id.btn_feature_next);

		imageFeatureView = (ImageView) findViewById(R.id.image_feature_view);

		// The Default Picture we show , haha
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hacker);

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

				NativeUtil.detectFeatures(mGray.getNativeObjAddr(),
						mRgba.getNativeObjAddr(), output.getNativeObjAddr());

				// Then convert the processed Mat to Bitmap
				Bitmap resultBitmap = Bitmap.createBitmap(output.cols(),
						output.rows(), Bitmap.Config.ARGB_8888);

				Utils.matToBitmap(output, resultBitmap);

				imageFeatureView.setImageBitmap(resultBitmap);
			}
		});

		btn_feature_next.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						DescriptorActivity.class);
				startActivity(intent);
			}

		});
	}

	public void loadImagefromGallery(View view) {
		// Create intent to Open Image applications like Gallery, Google Photos
		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// Start the Intent
		startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			// When an Image is picked
			if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
					&& null != data) {
				// Get the Image from data

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				// Get the cursor
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				// Move to first row
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				imgDecodableString = cursor.getString(columnIndex);
				Log.i(TAG, imgDecodableString);
				cursor.close();
				ImageView imgView = (ImageView) findViewById(R.id.image_feature_view);
				// Set the Image in ImageView after decoding the String
				bitmap = BitmapFactory.decodeFile(imgDecodableString);
				imgView.setImageBitmap(bitmap);

			} else {
				Toast.makeText(this, "Why Not You Pick An Image",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Something Wrong", Toast.LENGTH_LONG)
					.show();
		}

	}

}
