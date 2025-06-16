//#include <immintrin.h>
#include <stdint.h>
#include <stddef.h>
#include <math.h>
#include <cpuid.h>
#include <x86intrin.h>
#include "bagaturchess_nnue_v3_NNUE_SIMD_AVX2.h"

#define L1_SIZE 3072
#define WHITE 0
#define BLACK 1
#define COLOR_STRIDE (64 * 6)
#define PIECE_STRIDE 64

#define L1_SIZE 3072
#define L2_SIZE 15
#define FT_QUANT 255
#define FT_SHIFT 1
#define L1_QUANT 256
#define CLIPPED_MAX 1.0f


static int initialized = 0;
static int use_avx512 = 0;
	

int has_avx512f() {
    unsigned int eax, ebx, ecx, edx;

    // Check AVX and OS support
    __cpuid_count(1, 0, eax, ebx, ecx, edx);
    if (!(ecx & (1 << 28)) || !(ecx & (1 << 27))) return 0;

    // Check XGETBV enables AVX, YMM, ZMM
    //uint64_t xcr = _xgetbv(0);
    //if ((xcr & 0xE6) != 0xE6) return 0;

    // Check AVX512F bit
    __cpuid_count(7, 0, eax, ebx, ecx, edx);
    return (ebx & (1 << 16)) != 0;
}


static inline int getIndex(int square, int piece_side, int piece_type, int perspective) {
    int flipped_square = square ^ 0b111000;
    return (perspective == WHITE)
        ? piece_side * COLOR_STRIDE + piece_type * PIECE_STRIDE + square
        : (piece_side ^ 1) * COLOR_STRIDE + piece_type * PIECE_STRIDE + flipped_square;
}

void accumulate_internal_avx2(
    int16_t* restrict whiteAccumulator,
    int16_t* restrict blackAccumulator,
    const int16_t* restrict FTBiases,
    const int16_t* restrict FTWeights,
    const int32_t* restrict white_pieces,
    const int32_t* restrict white_squares,
    const int32_t* restrict black_pieces,
    const int32_t* restrict black_squares
) {
    // Initialize accumulators from biases
    for (int i = 0; i < L1_SIZE; i += 16) {
        __m256i bias = _mm256_loadu_si256((const __m256i*)&FTBiases[i]);
        _mm256_storeu_si256((__m256i*)&whiteAccumulator[i], bias);
        _mm256_storeu_si256((__m256i*)&blackAccumulator[i], bias);
    }

    for (int i = 0; i < 32; ++i) {
        int piece = white_pieces[i];
        if (piece == -1) break;
        int square = white_squares[i];
        int index_white = getIndex(square, WHITE, piece, WHITE);
        int index_black = getIndex(square, WHITE, piece, BLACK);
        const int16_t* weights_white = &FTWeights[index_white * L1_SIZE];
        const int16_t* weights_black = &FTWeights[index_black * L1_SIZE];

        for (int j = 0; j < L1_SIZE; j += 16) {
            __m256i w16 = _mm256_loadu_si256((const __m256i*)&weights_white[j]);
            __m256i b16 = _mm256_loadu_si256((const __m256i*)&weights_black[j]);

            __m256i w_lo = _mm256_cvtepi16_epi32(_mm256_extracti128_si256(w16, 0));
            __m256i w_hi = _mm256_cvtepi16_epi32(_mm256_extracti128_si256(w16, 1));
            __m256i b_lo = _mm256_cvtepi16_epi32(_mm256_extracti128_si256(b16, 0));
            __m256i b_hi = _mm256_cvtepi16_epi32(_mm256_extracti128_si256(b16, 1));

            __m256i accW_lo = _mm256_cvtepi16_epi32(_mm_loadu_si128((__m128i*)&whiteAccumulator[j]));
            __m256i accW_hi = _mm256_cvtepi16_epi32(_mm_loadu_si128((__m128i*)&whiteAccumulator[j + 8]));
            __m256i accB_lo = _mm256_cvtepi16_epi32(_mm_loadu_si128((__m128i*)&blackAccumulator[j]));
            __m256i accB_hi = _mm256_cvtepi16_epi32(_mm_loadu_si128((__m128i*)&blackAccumulator[j + 8]));

            accW_lo = _mm256_add_epi32(accW_lo, w_lo);
            accW_hi = _mm256_add_epi32(accW_hi, w_hi);
            accB_lo = _mm256_add_epi32(accB_lo, b_lo);
            accB_hi = _mm256_add_epi32(accB_hi, b_hi);

            _mm_storeu_si128((__m128i*)&whiteAccumulator[j], _mm256_cvtepi32_epi16(accW_lo));
            _mm_storeu_si128((__m128i*)&whiteAccumulator[j + 8], _mm256_cvtepi32_epi16(accW_hi));
            _mm_storeu_si128((__m128i*)&blackAccumulator[j], _mm256_cvtepi32_epi16(accB_lo));
            _mm_storeu_si128((__m128i*)&blackAccumulator[j + 8], _mm256_cvtepi32_epi16(accB_hi));
        }
    }

    for (int i = 0; i < 32; ++i) {
        int piece = black_pieces[i];
        if (piece == -1) break;
        int square = black_squares[i];
        int index_white = getIndex(square, BLACK, piece, WHITE);
        int index_black = getIndex(square, BLACK, piece, BLACK);
        const int16_t* weights_white = &FTWeights[index_white * L1_SIZE];
        const int16_t* weights_black = &FTWeights[index_black * L1_SIZE];

        for (int j = 0; j < L1_SIZE; j += 16) {
            __m256i w16 = _mm256_loadu_si256((const __m256i*)&weights_white[j]);
            __m256i b16 = _mm256_loadu_si256((const __m256i*)&weights_black[j]);

            __m256i w_lo = _mm256_cvtepi16_epi32(_mm256_extracti128_si256(w16, 0));
            __m256i w_hi = _mm256_cvtepi16_epi32(_mm256_extracti128_si256(w16, 1));
            __m256i b_lo = _mm256_cvtepi16_epi32(_mm256_extracti128_si256(b16, 0));
            __m256i b_hi = _mm256_cvtepi16_epi32(_mm256_extracti128_si256(b16, 1));

            __m256i accW_lo = _mm256_cvtepi16_epi32(_mm_loadu_si128((__m128i*)&whiteAccumulator[j]));
            __m256i accW_hi = _mm256_cvtepi16_epi32(_mm_loadu_si128((__m128i*)&whiteAccumulator[j + 8]));
            __m256i accB_lo = _mm256_cvtepi16_epi32(_mm_loadu_si128((__m128i*)&blackAccumulator[j]));
            __m256i accB_hi = _mm256_cvtepi16_epi32(_mm_loadu_si128((__m128i*)&blackAccumulator[j + 8]));

            accW_lo = _mm256_add_epi32(accW_lo, w_lo);
            accW_hi = _mm256_add_epi32(accW_hi, w_hi);
            accB_lo = _mm256_add_epi32(accB_lo, b_lo);
            accB_hi = _mm256_add_epi32(accB_hi, b_hi);

            _mm_storeu_si128((__m128i*)&whiteAccumulator[j], _mm256_cvtepi32_epi16(accW_lo));
            _mm_storeu_si128((__m128i*)&whiteAccumulator[j + 8], _mm256_cvtepi32_epi16(accW_hi));
            _mm_storeu_si128((__m128i*)&blackAccumulator[j], _mm256_cvtepi32_epi16(accB_lo));
            _mm_storeu_si128((__m128i*)&blackAccumulator[j + 8], _mm256_cvtepi32_epi16(accB_hi));
        }
    }
}


void accumulate_internal_avx512(
    int16_t* restrict whiteAccumulator,
    int16_t* restrict blackAccumulator,
    const int16_t* restrict FTBiases,
    const int16_t* restrict FTWeights,
    const int32_t* restrict white_pieces,
    const int32_t* restrict white_squares,
    const int32_t* restrict black_pieces,
    const int32_t* restrict black_squares
) {
    for (int i = 0; i < L1_SIZE; i += 32) {
        __m512i bias = _mm512_loadu_si512((const __m512i*)&FTBiases[i]);
        _mm512_storeu_si512((__m512i*)&whiteAccumulator[i], bias);
        _mm512_storeu_si512((__m512i*)&blackAccumulator[i], bias);
    }

    for (int i = 0; i < 32; ++i) {
        int piece = white_pieces[i];
        if (piece == -1) break;
        int square = white_squares[i];
        int idxW = getIndex(square, WHITE, piece, WHITE);
        int idxB = getIndex(square, WHITE, piece, BLACK);
        const int16_t* wW = &FTWeights[idxW * L1_SIZE];
        const int16_t* wB = &FTWeights[idxB * L1_SIZE];

        for (int j = 0; j < L1_SIZE; j += 32) {
            _mm_prefetch((const char*)&wW[j + 64], _MM_HINT_T0);
            _mm_prefetch((const char*)&wB[j + 64], _MM_HINT_T0);
            __m512i ww = _mm512_loadu_si512((const __m512i*)&wW[j]);
            __m512i wb = _mm512_loadu_si512((const __m512i*)&wB[j]);
            __m512i aW = _mm512_loadu_si512((__m512i*)&whiteAccumulator[j]);
            __m512i aB = _mm512_loadu_si512((__m512i*)&blackAccumulator[j]);
            aW = _mm512_add_epi16(aW, ww);
            aB = _mm512_add_epi16(aB, wb);
            _mm512_storeu_si512((__m512i*)&whiteAccumulator[j], aW);
            _mm512_storeu_si512((__m512i*)&blackAccumulator[j], aB);
        }
    }

    for (int i = 0; i < 32; ++i) {
        int piece = black_pieces[i];
        if (piece == -1) break;
        int square = black_squares[i];
        int idxW = getIndex(square, BLACK, piece, WHITE);
        int idxB = getIndex(square, BLACK, piece, BLACK);
        const int16_t* wW = &FTWeights[idxW * L1_SIZE];
        const int16_t* wB = &FTWeights[idxB * L1_SIZE];

        for (int j = 0; j < L1_SIZE; j += 32) {
			_mm_prefetch((const char*)&wW[j + 64], _MM_HINT_T0);
            _mm_prefetch((const char*)&wB[j + 64], _MM_HINT_T0);
            __m512i ww = _mm512_loadu_si512((const __m512i*)&wW[j]);
            __m512i wb = _mm512_loadu_si512((const __m512i*)&wB[j]);
            __m512i aW = _mm512_loadu_si512((__m512i*)&whiteAccumulator[j]);
            __m512i aB = _mm512_loadu_si512((__m512i*)&blackAccumulator[j]);
            aW = _mm512_add_epi16(aW, ww);
            aB = _mm512_add_epi16(aB, wb);
            _mm512_storeu_si512((__m512i*)&whiteAccumulator[j], aW);
            _mm512_storeu_si512((__m512i*)&blackAccumulator[j], aB);
        }
    }
}


JNIEXPORT void JNICALL Java_bagaturchess_nnue_1v3_NNUE_1SIMD_1AVX2_accumulate(
    JNIEnv *env,
    jobject thisObj,
    jshortArray whiteAccumulatorArray,
    jshortArray blackAccumulatorArray,
    jshortArray FTBiasesArray,
    jshortArray FTWeightsArray,
    jintArray whitePiecesArray,
    jintArray whiteSquaresArray,
    jintArray blackPiecesArray,
    jintArray blackSquaresArray
) {
	
	if (!initialized) {
        use_avx512 = has_avx512f();
        initialized = 1;
    }
	
    jint *whiteAccumulator = (*env)->GetPrimitiveArrayCritical(env, whiteAccumulatorArray, JNI_FALSE);
    jint *blackAccumulator = (*env)->GetPrimitiveArrayCritical(env, blackAccumulatorArray, JNI_FALSE);
    const jint *FTBiases = (*env)->GetPrimitiveArrayCritical(env, FTBiasesArray, JNI_FALSE);
    const jint *FTWeights = (*env)->GetPrimitiveArrayCritical(env, FTWeightsArray, JNI_FALSE);
    const jint *whitePieces = (*env)->GetPrimitiveArrayCritical(env, whitePiecesArray, JNI_FALSE);
    const jint *whiteSquares = (*env)->GetPrimitiveArrayCritical(env, whiteSquaresArray, JNI_FALSE);
    const jint *blackPieces = (*env)->GetPrimitiveArrayCritical(env, blackPiecesArray, JNI_FALSE);
    const jint *blackSquares = (*env)->GetPrimitiveArrayCritical(env, blackSquaresArray, JNI_FALSE);

	if (use_avx512) {
		accumulate_internal_avx512(
			(int16_t*)whiteAccumulator, (int16_t*)blackAccumulator,
			(const int16_t*)FTBiases, (const int16_t*)FTWeights,
			(const int32_t*)whitePieces, (const int32_t*)whiteSquares,
			(const int32_t*)blackPieces, (const int32_t*)blackSquares
		);
	} else {
		accumulate_internal_avx2(
			(int16_t*)whiteAccumulator, (int16_t*)blackAccumulator,
			(const int16_t*)FTBiases, (const int16_t*)FTWeights,
			(const int32_t*)whitePieces, (const int32_t*)whiteSquares,
			(const int32_t*)blackPieces, (const int32_t*)blackSquares
		);
	}

    (*env)->ReleasePrimitiveArrayCritical(env, whiteAccumulatorArray, whiteAccumulator, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, blackAccumulatorArray, blackAccumulator, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, FTBiasesArray, (jint*)FTBiases, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, FTWeightsArray, (jint*)FTWeights, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, whitePiecesArray, (jint*)whitePieces, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, whiteSquaresArray, (jint*)whiteSquares, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, blackPiecesArray, (jint*)blackPieces, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, blackSquaresArray, (jint*)blackSquares, JNI_ABORT);
}


void activateFTAndPropagateL1_internal_avx2(
    const int16_t* us,
    const int16_t* them,
    const int16_t* weights,
    const float* biases,
    float* output,
    int32_t* acc_int,
    int32_t* sumsL2
) {
    const float scale = (float)((FT_QUANT * FT_QUANT * L1_QUANT) >> FT_SHIFT);
    const int totalStride = L1_SIZE * L2_SIZE;

    for (int i = 0; i < L2_SIZE; i++) sumsL2[i] = 0;
    for (int i = 0; i < L1_SIZE; i++) acc_int[i] = 0;

    for (int p = 0; p < 2; p++) {
        const int16_t* acc = (p == 0) ? us : them;
        int weightOffset = p * totalStride;

        for (int i = 0; i < L1_SIZE; i += 16) {
            __m256i v = _mm256_loadu_si256((__m256i*)&acc[i]);
            __m256i zero = _mm256_setzero_si256();
            __m256i clipped = _mm256_min_epi16(_mm256_max_epi16(v, zero), _mm256_set1_epi16(FT_QUANT));
            __m256i lo = _mm256_cvtepi16_epi32(_mm256_castsi256_si128(clipped));
            __m256i hi = _mm256_cvtepi16_epi32(_mm256_extracti128_si256(clipped, 1));
            lo = _mm256_mullo_epi32(lo, lo);
            hi = _mm256_mullo_epi32(hi, hi);
            lo = _mm256_srai_epi32(lo, FT_SHIFT);
            hi = _mm256_srai_epi32(hi, FT_SHIFT);
            _mm256_storeu_si256((__m256i*)&acc_int[i], lo);
            _mm256_storeu_si256((__m256i*)&acc_int[i + 8], hi);
        }

        for (int out = 0; out < L2_SIZE; out++) {
            int sum = 0;
            int wOff = weightOffset + out * L1_SIZE;
            for (int i = 0; i < L1_SIZE; i++) {
                sum += acc_int[i] * weights[wOff + i];
            }
            sumsL2[out] += sum;
        }
    }

    for (int i = 0; i < L2_SIZE; i++) {
        float x = (sumsL2[i] / scale) + biases[i];
        if (x < 0.f) x = 0.f;
        else if (x > CLIPPED_MAX) x = CLIPPED_MAX;
        output[i] = x * x;
    }
}


void activateFTAndPropagateL1_internal_avx512(
    const int16_t* us,
    const int16_t* them,
    const int16_t* weights,
    const float* biases,
    float* output,
    int32_t* acc_int,
    int32_t* sumsL2
) {
    const float scale = (float)((FT_QUANT * FT_QUANT * L1_QUANT) >> FT_SHIFT);
    const int totalStride = L1_SIZE * L2_SIZE;

    // Zero out sumsL2 and acc_int
    for (int i = 0; i < L2_SIZE; i++)
        sumsL2[i] = 0;
    for (int i = 0; i < L1_SIZE; i++)
        acc_int[i] = 0;

    for (int p = 0; p < 2; p++) {
        const int16_t* acc = (p == 0) ? us : them;
        int weightOffset = p * totalStride;

        // Apply clipped square ReLU and convert to int32
        for (int i = 0; i < L1_SIZE; i += 32) {
            __m512i v = _mm512_loadu_si512((const __m512i*)&acc[i]);
            __m512i zero = _mm512_setzero_si512();
            __m512i clipped = _mm512_min_epi16(_mm512_max_epi16(v, zero), _mm512_set1_epi16(FT_QUANT));

            __m256i lo_256 = _mm512_extracti64x4_epi64(clipped, 0);
            __m256i hi_256 = _mm512_extracti64x4_epi64(clipped, 1);

            __m512i lo_32 = _mm512_cvtepi16_epi32(lo_256);
            __m512i hi_32 = _mm512_cvtepi16_epi32(hi_256);

            lo_32 = _mm512_mullo_epi32(lo_32, lo_32);
            hi_32 = _mm512_mullo_epi32(hi_32, hi_32);

            lo_32 = _mm512_srai_epi32(lo_32, FT_SHIFT);
            hi_32 = _mm512_srai_epi32(hi_32, FT_SHIFT);

            _mm512_storeu_si512((__m512i*)&acc_int[i], lo_32);
            _mm512_storeu_si512((__m512i*)&acc_int[i + 16], hi_32);
        }

        // Dot-product with weights
        for (int out = 0; out < L2_SIZE; out++) {
            int sum = 0;
            int wOff = weightOffset + out * L1_SIZE;

            for (int i = 0; i < L1_SIZE; i += 16) {
                __m512i acc_i32 = _mm512_loadu_si512((const __m512i*)&acc_int[i]);
                __m256i w_i16 = _mm256_loadu_si256((const __m256i*)&weights[wOff + i]);
                __m512i w_i32 = _mm512_cvtepi16_epi32(w_i16);
                __m512i mul = _mm512_mullo_epi32(acc_i32, w_i32);
                sum += _mm512_reduce_add_epi32(mul);
            }

            sumsL2[out] += sum;
        }
    }

    // Final layer activation
    for (int i = 0; i < L2_SIZE; i++) {
        float x = (sumsL2[i] / scale) + biases[i];
        if (x < 0.f) x = 0.f;
        else if (x > CLIPPED_MAX) x = CLIPPED_MAX;
        output[i] = x * x;
    }
}


JNIEXPORT void JNICALL Java_bagaturchess_nnue_1v3_NNUE_1SIMD_1AVX2_activateFTAndPropagateL1(
    JNIEnv *env,
    jobject thisObj,
    jshortArray usArray,
    jshortArray themArray,
    jshortArray weightsArray,
    jfloatArray biasesArray,
    jfloatArray outputArray,
    jintArray accIntArray,
    jintArray sumsL2Array
) {
	
	if (!initialized) {
        use_avx512 = has_avx512f();
        initialized = 1;
    }
	
    jshort* us = (*env)->GetPrimitiveArrayCritical(env, usArray, JNI_FALSE);
    jshort* them = (*env)->GetPrimitiveArrayCritical(env, themArray, JNI_FALSE);
    jshort* weights = (*env)->GetPrimitiveArrayCritical(env, weightsArray, JNI_FALSE);
    jfloat* biases = (*env)->GetPrimitiveArrayCritical(env, biasesArray, JNI_FALSE);
    jfloat* output = (*env)->GetPrimitiveArrayCritical(env, outputArray, JNI_FALSE);
    jint* acc_int = (*env)->GetPrimitiveArrayCritical(env, accIntArray, JNI_FALSE);
    jint* sumsL2 = (*env)->GetPrimitiveArrayCritical(env, sumsL2Array, JNI_FALSE);

	/*if (use_avx512) {
		activateFTAndPropagateL1_internal_avx512(
			(const int16_t*)us, (const int16_t*)them,
			(const int16_t*)weights, (const float*)biases,
			output,  (int32_t*)acc_int,  (int32_t*)sumsL2
		);
	} else {*/
		activateFTAndPropagateL1_internal_avx2(
			(const int16_t*)us, (const int16_t*)them,
			(const int16_t*)weights, (const float*)biases,
			output,  (int32_t*)acc_int,  (int32_t*)sumsL2
		);
	//}

    (*env)->ReleasePrimitiveArrayCritical(env, usArray, us, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, themArray, them, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, weightsArray, weights, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, biasesArray, biases, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, outputArray, output, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, accIntArray, acc_int, JNI_ABORT);
    (*env)->ReleasePrimitiveArrayCritical(env, sumsL2Array, sumsL2, JNI_ABORT);
}