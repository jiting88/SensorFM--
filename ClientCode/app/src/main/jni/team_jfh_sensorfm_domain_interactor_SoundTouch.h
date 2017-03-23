/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class team_jfh_sensorfm_domain_interactor_SoundTouch */

#ifndef _Included_team_jfh_sensorfm_domain_interactor_SoundTouch
#define _Included_team_jfh_sensorfm_domain_interactor_SoundTouch
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jstring JNICALL Java_team_jfh_sensorfm_domain_interactor_SoundTouch_getVersionString
  (JNIEnv *, jclass);


JNIEXPORT void JNICALL Java_team_jfh_sensorfm_domain_interactor_SoundTouch_setTempo
  (JNIEnv *, jobject, jlong, jfloat);


JNIEXPORT void JNICALL Java_team_jfh_sensorfm_domain_interactor_SoundTouch_setPitchSemiTones
  (JNIEnv *, jobject, jlong, jfloat);


JNIEXPORT void JNICALL Java_team_jfh_sensorfm_domain_interactor_SoundTouch_setSpeed
  (JNIEnv *, jobject, jlong, jfloat);


JNIEXPORT jint JNICALL Java_team_jfh_sensorfm_domain_interactor_SoundTouch_processFile
  (JNIEnv *, jobject, jlong, jstring, jstring);


JNIEXPORT jstring JNICALL Java_team_jfh_sensorfm_domain_interactor_SoundTouch_getErrorString
  (JNIEnv *, jclass);


JNIEXPORT jlong JNICALL Java_team_jfh_sensorfm_domain_interactor_SoundTouch_newInstance
  (JNIEnv *, jclass);


JNIEXPORT void JNICALL Java_team_jfh_sensorfm_domain_interactor_SoundTouch_deleteInstance
  (JNIEnv *, jobject, jlong);


JNIEXPORT jfloat JNICALL Java_team_jfh_sensorfm_domain_interactor_SoundTouch_getMyBPM
  (JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif