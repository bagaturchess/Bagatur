#include <jni.h>
#include <immintrin.h>
#include <stdint.h>
#include <cpuid.h>
#include <x86intrin.h>
#include "bagaturchess_nnue_v2_JNIUtils.h"


#define HIDDEN_SIZE 1024
#define QA 255


static __m256i qa_vec_avx2;
static __m256i zero_vec_avx2;
static int initialized_avx2 = 0;

static __m512i qa_vec_avx512;
static __m512i zero_vec_avx512;
static int initialized_avx512 = 0;


// Returns bitmask: bit 0 = AVX2, bit 1 = AVX512F
JNIEXPORT jint JNICALL Java_bagaturchess_nnue_1v2_JNIUtils_detectSIMD(JNIEnv *env, jclass clazz) {
    uint32_t eax, ebx, ecx, edx;
    jint result = 0;

    // CPUID(1): base check for AVX and OSXSAVE
    __cpuid_count(1, 0, eax, ebx, ecx, edx);
    if (!(ecx & (1 << 28))) return 0; // AVX not supported

    // CPUID(7,0): check AVX2 and AVX512F features
    __cpuid_count(7, 0, eax, ebx, ecx, edx);

    if (ebx & (1 << 5))    result |= 1; // AVX2
    if (ebx & (1 << 16))   result |= 2; // AVX512F

    return result;
}


JNIEXPORT jint JNICALL Java_bagaturchess_nnue_1v2_JNIUtils_evaluateVectorized_1avx512
  (JNIEnv *env, jclass clazz, jshortArray L2Weights, jshortArray UsValues, jshortArray ThemValues) {
	  
    jshort *l2_weights = (*env)->GetPrimitiveArrayCritical(env, L2Weights, JNI_FALSE);
    jshort *us_values = (*env)->GetPrimitiveArrayCritical(env, UsValues, JNI_FALSE);
    jshort *them_values = (*env)->GetPrimitiveArrayCritical(env, ThemValues, JNI_FALSE);

    if (!initialized_avx512) {
        qa_vec_avx512 = _mm512_set1_epi16(QA);
        zero_vec_avx512 = _mm512_setzero_si512();
        initialized_avx512 = 1;
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
        __m512i us_clamped = _mm512_max_epi16(zero_vec_avx512, _mm512_min_epi16(us_val, qa_vec_avx512));
        __m512i them_clamped = _mm512_max_epi16(zero_vec_avx512, _mm512_min_epi16(them_val, qa_vec_avx512));

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

    // Sum all elements in eval_vec using _mm512_reduce_add_epi32
    int eval = _mm512_reduce_add_epi32(eval_vec);

    (*env)->ReleasePrimitiveArrayCritical(env, L2Weights, l2_weights, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, UsValues, us_values, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, ThemValues, them_values, JNI_ABORT);

    return eval;
}


JNIEXPORT jint JNICALL Java_bagaturchess_nnue_1v2_JNIUtils_evaluateVectorized_1avx2
  (JNIEnv *env, jclass clazz, jshortArray L2Weights, jshortArray UsValues, jshortArray ThemValues) {

    jshort *l2_weights = (*env)->GetPrimitiveArrayCritical(env, L2Weights, JNI_FALSE);
    jshort *us_values = (*env)->GetPrimitiveArrayCritical(env, UsValues, JNI_FALSE);
    jshort *them_values = (*env)->GetPrimitiveArrayCritical(env, ThemValues, JNI_FALSE);

    if (!initialized_avx2) {
        qa_vec_avx2 = _mm256_set1_epi16(QA);
        zero_vec_avx2 = _mm256_setzero_si256();
        initialized_avx2 = 1;
    }

    __m256i eval_vec = _mm256_setzero_si256(); // Initialize eval_vec to zero

    for (int i = 0; i < HIDDEN_SIZE; i += 16) { // 16 × 16-bit per 256-bit register

        _mm_prefetch((char*)&us_values[i + 16], _MM_HINT_T0);
        _mm_prefetch((char*)&them_values[i + 16], _MM_HINT_T0);
        _mm_prefetch((char*)&l2_weights[i + 16], _MM_HINT_T0);
        _mm_prefetch((char*)&l2_weights[i + 16 + HIDDEN_SIZE], _MM_HINT_T0);

        __m256i us_val = _mm256_loadu_si256((__m256i*)&us_values[i]);
        __m256i them_val = _mm256_loadu_si256((__m256i*)&them_values[i]);

        __m256i us_clamped = _mm256_max_epi16(zero_vec_avx2, _mm256_min_epi16(us_val, qa_vec_avx2));
        __m256i them_clamped = _mm256_max_epi16(zero_vec_avx2, _mm256_min_epi16(them_val, qa_vec_avx2));

        __m256i l2_us = _mm256_loadu_si256((__m256i*)&l2_weights[i]);
        __m256i l2_them = _mm256_loadu_si256((__m256i*)&l2_weights[i + HIDDEN_SIZE]);

        __m256i us_weighted = _mm256_mullo_epi16(us_clamped, l2_us);
        __m256i them_weighted = _mm256_mullo_epi16(them_clamped, l2_them);

        __m256i us_result = _mm256_madd_epi16(us_weighted, us_clamped);
        __m256i them_result = _mm256_madd_epi16(them_weighted, them_clamped);

        eval_vec = _mm256_add_epi32(eval_vec, us_result);
        eval_vec = _mm256_add_epi32(eval_vec, them_result);
    }
	
    __m128i sum128 = _mm_add_epi32(_mm256_castsi256_si128(eval_vec), _mm256_extracti128_si256(eval_vec, 1));
    sum128 = _mm_add_epi32(sum128, _mm_shuffle_epi32(sum128, _MM_SHUFFLE(2, 3, 0, 1)));
    sum128 = _mm_add_epi32(sum128, _mm_shuffle_epi32(sum128, _MM_SHUFFLE(1, 0, 3, 2)));
    
	int eval = _mm_cvtsi128_si32(sum128);
	
    (*env)->ReleasePrimitiveArrayCritical(env, L2Weights, l2_weights, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, UsValues, us_values, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, ThemValues, them_values, JNI_ABORT);

    return eval;
}


JNIEXPORT void JNICALL Java_bagaturchess_nnue_1v2_JNIUtils_accumulateVectorized_1avx2
  (JNIEnv *env, jclass clazz, jshortArray acc, jshortArray weights, jboolean add) {

    jshort *acc_ptr = (*env)->GetPrimitiveArrayCritical(env, acc, JNI_FALSE);
    jshort *weights_ptr = (*env)->GetPrimitiveArrayCritical(env, weights, JNI_FALSE);

    for (int i = 0; i < HIDDEN_SIZE; i += 16) {
        __m256i w = _mm256_loadu_si256((__m256i*)&weights_ptr[i]);
        __m256i a = _mm256_loadu_si256((__m256i*)&acc_ptr[i]);
        __m256i res;

        if (add) {
            res = _mm256_add_epi16(a, w);
        } else {
            res = _mm256_sub_epi16(a, w);
        }

        _mm256_storeu_si256((__m256i*)&acc_ptr[i], res);
    }

    (*env)->ReleasePrimitiveArrayCritical(env, acc, acc_ptr, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, weights, weights_ptr, JNI_ABORT);
}


JNIEXPORT void JNICALL Java_bagaturchess_nnue_1v2_JNIUtils_accumulateVectorized_1avx512
  (JNIEnv *env, jclass clazz, jshortArray acc, jshortArray weights, jboolean add) {

    jshort *acc_ptr = (*env)->GetPrimitiveArrayCritical(env, acc, JNI_FALSE);
    jshort *weights_ptr = (*env)->GetPrimitiveArrayCritical(env, weights, JNI_FALSE);

    for (int i = 0; i < HIDDEN_SIZE; i += 32) {
        __m512i w = _mm512_loadu_si512((__m512i*)&weights_ptr[i]);
        __m512i a = _mm512_loadu_si512((__m512i*)&acc_ptr[i]);
        __m512i res;

        if (add) {
            res = _mm512_add_epi16(a, w);
        } else {
            res = _mm512_sub_epi16(a, w);
        }

        _mm512_storeu_si512((__m512i*)&acc_ptr[i], res);
    }

    (*env)->ReleasePrimitiveArrayCritical(env, acc, acc_ptr, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, weights, weights_ptr, JNI_ABORT);
}