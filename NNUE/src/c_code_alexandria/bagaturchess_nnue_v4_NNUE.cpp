#include <jni.h>
#include "bagaturchess_nnue_v4_NNUE.h"
#include "nnue.h"

extern "C" {

JNIEXPORT jint JNICALL Java_bagaturchess_nnue_1v4_NNUE_init
  (JNIEnv *env, jclass clazz, jstring filePath) {
    const char *nativeFilePath = env->GetStringUTFChars(filePath, 0);

    // Call the NNUE::init method
    NNUE nnue;
    nnue.init(nativeFilePath);

    // Release the Java string
    env->ReleaseStringUTFChars(filePath, nativeFilePath);

    // Return a success code (0 for success)
    return 0;
}

JNIEXPORT jint JNICALL Java_bagaturchess_nnue_1v4_NNUE_eval
  (JNIEnv *env, jclass clazz, jshortArray jUs, jshortArray jThem, jint outputBucket) {
    
    // Get the size of the arrays
    jsize usSize = env->GetArrayLength(jUs);
    jsize themSize = env->GetArrayLength(jThem);

    // Ensure the sizes are correct
    if (usSize != L1_SIZE || themSize != L1_SIZE) {
        // Throw an exception if sizes are incorrect
        jclass illegalArgumentException = env->FindClass("java/lang/IllegalArgumentException");
        std::string errorMsg = "Array sizes must be " + std::to_string(L1_SIZE);
        env->ThrowNew(illegalArgumentException, errorMsg.c_str());
        return 0;
    }

    // Get the array elements
    jshort* us = env->GetShortArrayElements(jUs, nullptr);
    jshort* them = env->GetShortArrayElements(jThem, nullptr);

    // Call the NNUE::output method
    NNUE nnue;
    int32_t result = nnue.output(reinterpret_cast<int16_t*>(us), reinterpret_cast<int16_t*>(them), outputBucket);

    // Release the array elements
    env->ReleaseShortArrayElements(jUs, us, 0);
    env->ReleaseShortArrayElements(jThem, them, 0);

    return result;
}

} // extern "C"
