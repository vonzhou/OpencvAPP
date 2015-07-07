package com.example.test;

import java.io.File;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class DescriptorActivity extends Activity {
	private Button btn_descriptor;
	private Button btn_extract_all; // choose a folder and get all the
									// descriptors
	private EditText editText ;
	private ImageView imageView;
	private Bitmap bitmap;
	private static final String TAG = "Feature Descriptor::Activity";
	private static final String IMAGE_DIR = "/mnt/sdcard/imageset/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_descriptor);
		// Set background color
		getWindow().getDecorView().setBackgroundColor(Color.LTGRAY);

		btn_descriptor = (Button) findViewById(R.id.btn_descriptor);
		btn_extract_all = (Button) findViewById(R.id.btn_extract_all_descriptors);
		editText = (EditText)findViewById(R.id.factor);
		imageView = (ImageView) findViewById(R.id.descriptor_image_view);

		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wheat);

		imageView.setImageBitmap(bitmap);

		btn_descriptor.setOnClickListener(new Button.OnClickListener() {

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

				NativeUtil.computeDescripors(mGray.getNativeObjAddr(),
						mRgba.getNativeObjAddr(), output.getNativeObjAddr());

				Log.i(TAG, "Row:" + output.rows() + ",Col:" + output.cols()
						+ ",channels:" + output.channels());

				// Then convert the processed Mat to Bitmap
				Bitmap resultBitmap = Bitmap.createBitmap(output.cols(),
						output.rows(), Bitmap.Config.ARGB_8888);

				Utils.matToBitmap(output, resultBitmap);

				imageView.setImageBitmap(resultBitmap);
			}
		});

		btn_extract_all.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// get the factor from the EditText input
				String factor = editText.getText().toString();
				//double factor = Double.parseDouble(input);
				long startTime = System.currentTimeMillis();
				iterateImages(factor);
				long endTime = System.currentTimeMillis();
				long totalTime = endTime - startTime;
				showDialog("Cost:" + totalTime/1000d + " seconds.");
			}
		});
	}

	public void iterateImages(String factor) {
		File dir = new File(IMAGE_DIR + "resize" + factor + "/");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File file : directoryListing) {
				Bitmap bitmap = BitmapFactory
						.decodeFile(file.getAbsolutePath());
				extractImageORBDescriptors(bitmap);
				 Log.i(TAG, file.getName());
				// Do something with child
			}
		} else {
			// Handle the case where dir is not really a directory.
			// Checking dir.isDirectory() above would not be sufficient
			// to avoid race conditions with another process that deletes
			// directories.
		}
		

	}

	public Bitmap extractImageORBDescriptors(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Mat mRgba = new Mat(height, width, CvType.CV_8U, new Scalar(4));
		Mat mGray = new Mat(height, width, CvType.CV_8U, new Scalar(4));
		Mat output = new Mat();

		Utils.bitmapToMat(bitmap, mRgba);

		// do sth
		// Converts an image from one color space to another.
		Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGB2GRAY, 4);

		NativeUtil.computeDescripors(mGray.getNativeObjAddr(),
				mRgba.getNativeObjAddr(), output.getNativeObjAddr());

		// Then convert the processed Mat to Bitmap
		Bitmap resultBitmap = Bitmap.createBitmap(output.cols(), output.rows(),
				Bitmap.Config.ARGB_8888);
		Log.i(TAG, "Width:"+resultBitmap.getWidth() +", Height:" + resultBitmap.getHeight());

		Utils.matToBitmap(output, resultBitmap);

		return resultBitmap;
	}

	public void showDialog(String str) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);

		// set title
		alertDialogBuilder.setTitle("Done");

		// set dialog message
		alertDialogBuilder
				.setMessage("All Descriptors Extracted! "+str)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								DescriptorActivity.this.finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

}
