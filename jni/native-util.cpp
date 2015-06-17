#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/nonfree/features2d.hpp> //
#include <string>
#include <iostream>
#include "com_example_test_NativeUtil.h"

using namespace std;
using namespace cv;
extern "C" {

JNIEXPORT jintArray JNICALL Java_com_example_test_NativeUtil_transformToGray(
		JNIEnv *env, jclass obj, jintArray pixels, jint width, jint height) {

	jboolean b;
	jint *buf;
	buf = env->GetIntArrayElements(pixels, &b);
	if (buf == NULL) {
		return 0;
	}

	//create the Mat and use your int array as input
	Mat imgData(height, width, CV_8UC4, (unsigned char*) buf);

	SiftFeatureDetector detector;
	vector<KeyPoint> keypoints;
	detector.detect(input, keypoints);

	// show the keypoints on an image
	Mat output;
	drawKeypoints(input, keypoints, output);

	int size = width * height;
	jintArray result = env->NewIntArray(size);
	env->SetIntArrayRegion(result, 0, size, buf);
	env->ReleaseIntArrayElements(pixels, buf, 0);

	return result;
}

JNIEXPORT jstring JNICALL Java_com_example_test_NativeUtil_stringFromJNI(
		JNIEnv *env, jclass obj) {
	return env->NewStringUTF("Hello from JNI !");

}

}
