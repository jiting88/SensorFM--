/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class SensorServer_utility_SoundTouch */

#ifndef _Included_SensorServer_utility_SoundTouch
#define _Included_SensorServer_utility_SoundTouch
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     SensorServer_utility_SoundTouch
 * Method:    getVersionString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_SensorServer_utility_SoundTouch_getVersionString
  (JNIEnv *, jclass);

/*
 * Class:     SensorServer_utility_SoundTouch
 * Method:    setTempo
 * Signature: (JF)V
 */
JNIEXPORT void JNICALL Java_SensorServer_utility_SoundTouch_setTempo
  (JNIEnv *, jobject, jlong, jfloat);

/*
 * Class:     SensorServer_utility_SoundTouch
 * Method:    setPitchSemiTones
 * Signature: (JF)V
 */
JNIEXPORT void JNICALL Java_SensorServer_utility_SoundTouch_setPitchSemiTones
  (JNIEnv *, jobject, jlong, jfloat);

/*
 * Class:     SensorServer_utility_SoundTouch
 * Method:    setSpeed
 * Signature: (JF)V
 */
JNIEXPORT void JNICALL Java_SensorServer_utility_SoundTouch_setSpeed
  (JNIEnv *, jobject, jlong, jfloat);

/*
 * Class:     SensorServer_utility_SoundTouch
 * Method:    processFile
 * Signature: (JLjava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_SensorServer_utility_SoundTouch_processFile
  (JNIEnv *, jobject, jlong, jstring, jstring);

/*
 * Class:     SensorServer_utility_SoundTouch
 * Method:    getErrorString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_SensorServer_utility_SoundTouch_getErrorString
  (JNIEnv *, jclass);

/*
 * Class:     SensorServer_utility_SoundTouch
 * Method:    newInstance
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_SensorServer_utility_SoundTouch_newInstance
  (JNIEnv *, jclass);

/*
 * Class:     SensorServer_utility_SoundTouch
 * Method:    deleteInstance
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_SensorServer_utility_SoundTouch_deleteInstance
  (JNIEnv *, jobject, jlong);

/*
 * Class:     SensorServer_utility_SoundTouch
 * Method:    getMyBPM
 * Signature: (Ljava/lang/String;)F
 */
JNIEXPORT jfloat JNICALL Java_SensorServer_utility_SoundTouch_getMyBPM
  (JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif
