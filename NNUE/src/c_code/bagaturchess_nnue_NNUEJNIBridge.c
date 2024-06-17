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


JNIEXPORT jint JNICALL Java_bagaturchess_nnue_NNUEJNIBridge_eval__Ljava_lang_String_2
	(JNIEnv *env, jclass this_class, jstring fen_str) {

	  const char *fen = env->GetStringUTFChars(fen_str, NULL);

	  int result = nnue_evaluate_fen(fen);

	  env->ReleaseStringUTFChars(fen_str, fen);

	  return result;
  }
  
  
JNIEXPORT jint JNICALL Java_bagaturchess_nnue_NNUEJNIBridge_eval__I_3I_3I
  (JNIEnv *env, jclass this_class, jint color, jintArray pieces, jintArray squares) {
  
	  int c_color = (int)color;
	  jint *c_pieces = env->GetIntArrayElements(pieces, NULL);
	  jint *c_squares = env->GetIntArrayElements(squares, NULL);
  
	  int result = nnue_evaluate(c_color, (int*)c_pieces, (int*)c_squares);
  
	  env->ReleaseIntArrayElements(pieces, c_pieces, 0);
	  env->ReleaseIntArrayElements(squares, c_squares, 0);
	  
	  return result;
  }
