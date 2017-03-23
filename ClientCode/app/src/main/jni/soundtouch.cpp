//
// Created by jicl on 16/9/9.
//
#include <jni.h>
#include <android/log.h>
#include <stdexcept>
#include <string>
#include "SoundTouch/SoundTouch.h"
#include "SoundTouch/WavFile.h"
#include "SoundTouch/BPMDetect.h"
#include "team_jfh_sensorfm_domain_interactor_SoundTouch.h"
#define LOGV(...)   __android_log_print((int)ANDROID_LOG_INFO, "SOUNDTOUCH", __VA_ARGS__)
//#define LOGV(...)


// String for keeping possible c++ exception error messages. Notice that this isn't
// thread-safe but it's expected that exceptions are special situations that won't
// occur in several threads in parallel.


#define DLL_PUBLIC __attribute__ ((visibility ("default")))
#define BUFF_SIZE 4096


using namespace std;
using namespace soundtouch;

static string _errMsg = "";

// Set error message to return
static void _setErrmsg(const char *msg)
{
	_errMsg = msg;
}


#ifdef _OPENMP

#include <pthread.h>
extern pthread_key_t gomp_tls_key;
static void * _p_gomp_tls = NULL;
static int _init_threading(bool warn)
{
	void *ptr = pthread_getspecific(gomp_tls_key);
	LOGV("JNI thread-specific TLS storage %ld", (long)ptr);
	if (ptr == NULL)
	{
		LOGV("JNI set missing TLS storage to %ld", (long)_p_gomp_tls);
		pthread_setspecific(gomp_tls_key, _p_gomp_tls);
	}
	else
	{
		LOGV("JNI store this TLS storage");
		_p_gomp_tls = ptr;
	}
	// Where critical, show warning if storage still not properly initialized
	if ((warn) && (_p_gomp_tls == NULL))
	{
		_setErrmsg("Error - OpenMP threading not properly initialized: Call SoundTouch.getVersionString() from the App main thread!");
		return -1;
	}
	return 0;
}

#else
static int _init_threading(bool warn)
{
	// do nothing if not OpenMP build
	return 0;
}
#endif


// Processes the sound file
static void _processFile(SoundTouch *pSoundTouch, const char *inFileName, const char *outFileName)
{
    int nSamples;
    int nChannels;
    int buffSizeSamples;
    SAMPLETYPE sampleBuffer[BUFF_SIZE];

    // open input file
    WavInFile inFile(inFileName);
    int sampleRate = inFile.getSampleRate();
    int bits = inFile.getNumBits();
    nChannels = inFile.getNumChannels();

    // create output file
    WavOutFile outFile(outFileName, sampleRate, bits, nChannels);

    pSoundTouch->setSampleRate(sampleRate);
    pSoundTouch->setChannels(nChannels);

    assert(nChannels > 0);
    buffSizeSamples = BUFF_SIZE / nChannels;

    // Process samples read from the input file
    while (inFile.eof() == 0)
    {
        int num;

        // Read a chunk of samples from the input file
        num = inFile.read(sampleBuffer, BUFF_SIZE);
        nSamples = num / nChannels;

        // Feed the samples into SoundTouch processor
        pSoundTouch->putSamples(sampleBuffer, nSamples);

        // Read ready samples from SoundTouch processor & write them output file.
        // NOTES:
        // - 'receiveSamples' doesn't necessarily return any samples at all
        //   during some rounds!
        // - On the other hand, during some round 'receiveSamples' may have more
        //   ready samples than would fit into 'sampleBuffer', and for this reason
        //   the 'receiveSamples' call is iterated for as many times as it
        //   outputs samples.
        do
        {
            nSamples = pSoundTouch->receiveSamples(sampleBuffer, buffSizeSamples);
            outFile.write(sampleBuffer, nSamples * nChannels);
        } while (nSamples != 0);
    }

    // Now the input file is processed, yet 'flush' few last samples that are
    // hiding in the SoundTouch's internal processing pipeline.
    pSoundTouch->flush();
    do
    {
        nSamples = pSoundTouch->receiveSamples(sampleBuffer, buffSizeSamples);
        outFile.write(sampleBuffer, nSamples * nChannels);
    } while (nSamples != 0);
}

/*
 * Class:     team_jfh_sensorfm_domain_interactor_SoundTouch
 * Method:    getVersionString
 * Signature: ()Ljava/lang/String;
 */
extern "C" DLL_PUBLIC jstring Java_team_jfh_sensorfm_domain_interactor_SoundTouch_getVersionString(JNIEnv *env, jclass thiz)
{
    const char *verStr;

    LOGV("JNI call SoundTouch.getVersionString");

    // Call example SoundTouch routine
    verStr = SoundTouch::getVersionString();

    /// gomp_tls storage bug workaround - see comments in _init_threading() function!
    _init_threading(false);

    int threads = 0;
	#pragma omp parallel
    {
		#pragma omp atomic
    	threads ++;
    }
    LOGV("JNI thread count %d", threads);

    // return version as string
    return env->NewStringUTF(verStr);
}

/*
 * Class:     team_jfh_sensorfm_domain_interactor_SoundTouch
 * Method:    newInstance
 * Signature: ()J
 */
extern "C" DLL_PUBLIC jlong Java_team_jfh_sensorfm_domain_interactor_SoundTouch_newInstance(JNIEnv *env, jclass thiz)
{
	return (jlong)(new SoundTouch());
}

/*
 * Class:     team_jfh_sensorfm_domain_interactor_SoundTouch
 * Method:    deleteInstance
 * Signature: (J)V
 */
extern "C" DLL_PUBLIC void Java_team_jfh_sensorfm_domain_interactor_SoundTouch_deleteInstance(JNIEnv *env, jobject thiz, jlong handle)
{
	SoundTouch *ptr = (SoundTouch*)handle;
	delete ptr;
}

/*
 * Class:     team_jfh_sensorfm_domain_interactor_SoundTouch
 * Method:    setTempo
 * Signature: (JF)V
 */
extern "C" DLL_PUBLIC void Java_team_jfh_sensorfm_domain_interactor_SoundTouch_setTempo(JNIEnv *env, jobject thiz, jlong handle, jfloat tempo)
{
	SoundTouch *ptr = (SoundTouch*)handle;
	ptr->setTempo(tempo);
}

/*
 * Class:     team_jfh_sensorfm_domain_interactor_SoundTouch
 * Method:    setPitchSemiTones
 * Signature: (JF)V
 */
extern "C" DLL_PUBLIC void Java_team_jfh_sensorfm_domain_interactor_SoundTouch_setPitchSemiTones(JNIEnv *env, jobject thiz, jlong handle, jfloat pitch)
{
	SoundTouch *ptr = (SoundTouch*)handle;
	ptr->setPitchSemiTones(pitch);
}

/*
 * Class:     team_jfh_sensorfm_domain_interactor_SoundTouch
 * Method:    setSpeed
 * Signature: (JF)V
 */
extern "C" DLL_PUBLIC void Java_team_jfh_sensorfm_domain_interactor_SoundTouch_setSpeed(JNIEnv *env, jobject thiz, jlong handle, jfloat speed)
{
	SoundTouch *ptr = (SoundTouch*)handle;
	ptr->setRate(speed);
}

/*
 * Class:     team_jfh_sensorfm_domain_interactor_SoundTouch
 * Method:    getErrorString
 * Signature: ()Ljava/lang/String;
 */
extern "C" DLL_PUBLIC jstring Java_team_jfh_sensorfm_domain_interactor_SoundTouch_getErrorString(JNIEnv *env, jclass thiz)
{
	jstring result = env->NewStringUTF(_errMsg.c_str());
	_errMsg.clear();

	return result;
}

/*
 * Class:     team_jfh_sensorfm_domain_interactor_SoundTouch
 * Method:    processFile
 * Signature: (JLjava/lang/String;Ljava/lang/String;)I
 */
extern "C" DLL_PUBLIC int Java_team_jfh_sensorfm_domain_interactor_SoundTouch_processFile(JNIEnv *env, jobject thiz, jlong handle, jstring jinputFile, jstring joutputFile)
{
	SoundTouch *ptr = (SoundTouch*)handle;

	const char *inputFile = env->GetStringUTFChars(jinputFile, 0);
	const char *outputFile = env->GetStringUTFChars(joutputFile, 0);

	LOGV("JNI process file %s", inputFile);

    /// gomp_tls storage bug workaround - see comments in _init_threading() function!
    if (_init_threading(true)) return -1;

	try
	{
		_processFile(ptr, inputFile, outputFile);
	}
	catch (const runtime_error &e)
    {
		const char *err = e.what();
        // An exception occurred during processing, return the error message
    	LOGV("JNI exception in SoundTouch::processFile: %s", err);
        _setErrmsg(err);
        return -1;
    }


	env->ReleaseStringUTFChars(jinputFile, inputFile);
	env->ReleaseStringUTFChars(joutputFile, outputFile);

	return 0;
}

/*
 * Class:     team_jfh_sensorfm_domain_interactor_SoundTouch
 * Method:    getMyBPM
 * Signature: (Ljava/lang/String;)F
 */
extern "C" DLL_PUBLIC jfloat Java_team_jfh_sensorfm_domain_interactor_SoundTouch_getMyBPM(JNIEnv* env,jclass thiz, jstring inFileName )
{
    const char *inFilechar = env->GetStringUTFChars(inFileName, 0);


    WavInFile inFileobj(inFilechar);
    WavInFile * inFile=&inFileobj;
    float bpmValue;
    int nChannels;
    BPMDetect bpm(inFile->getNumChannels(), inFile->getSampleRate());
    SAMPLETYPE sampleBuffer[BUFF_SIZE];


    // detect bpm rate
    fprintf(stderr, "Detecting BPM rate...");
    fflush(stderr);


    nChannels = (int)inFile->getNumChannels();
    assert(BUFF_SIZE % nChannels == 0);


    // Process the 'inFile' in small blocks, repeat until whole file has
    // been processed
    while (inFile->eof() == 0)
    {
        int num, samples;


        // Read sample data from input file
        num = inFile->read(sampleBuffer, BUFF_SIZE);


        // Enter the new samples to the bpm analyzer class
        samples = num / nChannels;
        bpm.inputSamples(sampleBuffer, samples);
    }


    // Now the whole song data has been analyzed. Read the resulting bpm.
    bpmValue = bpm.getBpm();
    return jfloat(bpmValue);


}
