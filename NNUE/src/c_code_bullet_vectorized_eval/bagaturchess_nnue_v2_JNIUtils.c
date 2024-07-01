#include <jni.h>
#include <immintrin.h>
#include "bagaturchess_nnue_v2_JNIUtils.h"

#define HIDDEN_SIZE 1024
#define QA 255

static __m512i qa_vec;
static __m512i zero_vec;
static int initialized = 0;

JNIEXPORT jint JNICALL Java_bagaturchess_nnue_1v2_JNIUtils_evaluateVectorized
  (JNIEnv *env, jclass clazz, jshortArray L2Weights, jshortArray UsValues, jshortArray ThemValues, jintArray evalVec) {
	  
    jshort *l2_weights = (*env)->GetShortArrayElements(env, L2Weights, NULL);
    jshort *us_values = (*env)->GetShortArrayElements(env, UsValues, NULL);
    jshort *them_values = (*env)->GetShortArrayElements(env, ThemValues, NULL);
    jint *eval_vec_array = (*env)->GetIntArrayElements(env, evalVec, NULL);

    if (!initialized) {
        qa_vec = _mm512_set1_epi16(QA);
        zero_vec = _mm512_setzero_si512();
        initialized = 1;
    }

    __m512i eval_vec = _mm512_setzero_si512(); // Initialize eval_vec to zero

    for (int i = 0; i < HIDDEN_SIZE; i += 32) { // Process 32 elements at a time
        // Prefetch next data to cache
        _mm_prefetch((char*)&us_values[i + 32], _MM_HINT_T0);
        _mm_prefetch((char*)&them_values[i + 32], _MM_HINT_T0);
        _mm_prefetch((char*)&l2_weights[i + 32], _MM_HINT_T0);
        _mm_prefetch((char*)&l2_weights[i + 32 + HIDDEN_SIZE], _MM_HINT_T0);

        // Load values
        __m512i us_val = _mm512_loadu_si512((__m512i*) &us_values[i]);
        __m512i them_val = _mm512_loadu_si512((__m512i*) &them_values[i]);

        // Clamp values to [0, QA]
        __m512i us_clamped = _mm512_max_epi16(zero_vec, _mm512_min_epi16(us_val, qa_vec));
        __m512i them_clamped = _mm512_max_epi16(zero_vec, _mm512_min_epi16(them_val, qa_vec));

        // Load weights
        __m512i l2_weights_us = _mm512_loadu_si512((__m512i*) &l2_weights[i]);
        __m512i l2_weights_them = _mm512_loadu_si512((__m512i*) &l2_weights[i + HIDDEN_SIZE]);

        // Multiply us_clamped and l2_weights_us, them_clamped and l2_weights_them
        __m512i us_weighted = _mm512_mullo_epi16(us_clamped, l2_weights_us);
        __m512i them_weighted = _mm512_mullo_epi16(them_clamped, l2_weights_them);

        // Multiply-and-add operation for us_weighted and them_weighted with themselves
        __m512i us_result = _mm512_madd_epi16(us_weighted, us_clamped);
        __m512i them_result = _mm512_madd_epi16(them_weighted, them_clamped);

        // Accumulate results
        eval_vec = _mm512_add_epi32(eval_vec, us_result);
        eval_vec = _mm512_add_epi32(eval_vec, them_result);
    }

    // Store the result back to eval_vec_array
    _mm512_storeu_si512((__m512i*)eval_vec_array, eval_vec);

    (*env)->ReleaseShortArrayElements(env, L2Weights, l2_weights, 0);
    (*env)->ReleaseShortArrayElements(env, UsValues, us_values, 0);
    (*env)->ReleaseShortArrayElements(env, ThemValues, them_values, 0);
    (*env)->ReleaseIntArrayElements(env, evalVec, eval_vec_array, 0);

    // Return the sum of all elements in eval_vec_array
    int eval = 0;
    for (int i = 0; i < 16; i++) { // Adjust to 16 as AVX-512 processes 16 32-bit integers at a time
        eval += eval_vec_array[i];
    }

    return eval;
}
