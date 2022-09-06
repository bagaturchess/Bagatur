

#include <jni.h>
#include <tbprobe.h>
#include <tbprobe.c>


JNIEXPORT jboolean JNICALL Java_bagaturchess_egtb_syzygy_SyzygyJNIBridge_init
  (JNIEnv * env, jclass this_class, jstring path) {

    const char *native_path = (*env)->GetStringUTFChars(env, path, false);

    jboolean result = tb_init(native_path);

    (*env)->ReleaseStringUTFChars(env, path, native_path);

    return result;
  }


JNIEXPORT jint JNICALL Java_bagaturchess_egtb_syzygy_SyzygyJNIBridge_getTBLargest
  (JNIEnv * env, jclass this_class) {

    return TB_LARGEST;
  }


JNIEXPORT jint JNICALL Java_bagaturchess_egtb_syzygy_SyzygyJNIBridge_probeDTM
(JNIEnv* env, jclass this_class, jlong white, jlong black, jlong kings, jlong queens, jlong rooks, jlong bishops, jlong knights, jlong pawns, jint rule50, jint ep, jboolean color_to_move) {

    return tb_probe_dtm_win_impl(white, black, kings, queens, rooks, bishops, knights, pawns, ep, color_to_move);
}


JNIEXPORT jint JNICALL Java_bagaturchess_egtb_syzygy_SyzygyJNIBridge_probeWDL
  (JNIEnv * env, jclass this_class, jlong white, jlong black, jlong kings, jlong queens, jlong rooks, jlong bishops, jlong knights, jlong pawns, jint rule50, jint ep, jboolean color_to_move) {

    return tb_probe_wdl_impl(white, black, kings, queens, rooks, bishops, knights, pawns, ep, color_to_move);
  }


JNIEXPORT jint JNICALL Java_bagaturchess_egtb_syzygy_SyzygyJNIBridge_probeDTZ
  (JNIEnv * env, jclass this_class, jlong white, jlong black, jlong kings, jlong queens, jlong rooks, jlong bishops, jlong knights, jlong pawns, jint rule50, jint ep, jboolean color_to_move) {

    return tb_probe_dtz_impl(white, black, kings, queens, rooks, bishops, knights, pawns, ep, color_to_move);
  }
