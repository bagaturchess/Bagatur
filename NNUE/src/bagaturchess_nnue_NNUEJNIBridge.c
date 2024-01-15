#include "jni.h"
#include "nnue.h"
#include "nnue.cpp"
#include "misc.h"
#include "misc.cpp"
#include "bagaturchess_nnue_NNUEJNIBridge.h"


JNIEXPORT void JNICALL Java_bagaturchess_nnue_NNUEJNIBridge_init
  (JNIEnv *env, jclass this_class, jstring filename) {

    const char *nnue_filename = env->GetStringUTFChars(filename, NULL);

    nnue_init(nnue_filename);

    env->ReleaseStringUTFChars(filename, nnue_filename);
}


JNIEXPORT jint JNICALL Java_bagaturchess_nnue_NNUEJNIBridge_eval
	(JNIEnv *env, jclass this_class, jstring fen_str) {

	  const char *fen = env->GetStringUTFChars(fen_str, NULL);

	  int result = nnue_evaluate_fen(fen);

	  env->ReleaseStringUTFChars(fen_str, fen);

	  return result;
  }
