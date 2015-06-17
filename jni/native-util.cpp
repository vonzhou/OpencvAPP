#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/features2d/features2d.hpp>

#include <string>
#include <iostream>
#include "com_example_test_NativeUtil.h"

using namespace std;
using namespace cv;
extern "C" {

JNIEXPORT void JNICALL Java_com_example_test_NativeUtil_detectFeatures(
		JNIEnv *env, jclass thiz, jlong mGrayAddr, jlong mRgbaAddr, jlong mOutputAddr) {
	Mat* pMatGr=(Mat*)mGrayAddr;
	Mat* pMatRgb=(Mat*)mRgbaAddr;
	Mat* pMatDesc=(Mat*)mOutputAddr;
	vector<KeyPoint> v;

	//OrbFeatureDetector detector(50);
	OrbFeatureDetector detector;
	OrbDescriptorExtractor extractor;
	detector.detect(*pMatGr, v);
	/*
	extractor.compute( *pMatGr, v, *pMatDesc );
	circle(*pMatRgb, Point(100,100), 10, Scalar(5,128,255,255));
	for( size_t i = 0; i < v.size(); i++ ) {
		circle(*pMatRgb, Point(v[i].pt.x, v[i].pt.y), 10, Scalar(255,128,0,255));
	}
	*/
	drawKeypoints(*pMatGr, v, *pMatDesc);
}


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
