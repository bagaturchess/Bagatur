#pragma once
#include <cstdint>

#if defined(USE_SIMD)
#include <immintrin.h>
#endif

#if defined(USE_AVX512)
using vepi16 = __m512i;
using vepi32 = __m512i;
using vps32  = __m256;

inline vepi16 vec_zero_epi16() { return _mm512_setzero_si512(); }
inline vepi32 vec_zero_epi32() { return _mm512_setzero_si512(); }
inline vepi16 vec_set1_epi16  (const int16_t n) { return _mm512_set1_epi16(n); }
inline vepi16 vec_load_epi    (const vepi16 *src) { return _mm512_load_si512(src); }
inline vepi16 vec_max_epi16   (const vepi16 vec0, const vepi16 vec1) { return _mm512_max_epi16(vec0, vec1); }
inline vepi16 vec_min_epi16   (const vepi16 vec0, const vepi16 vec1) { return _mm512_min_epi16(vec0, vec1); }
inline vepi16 vec_mullo_epi16 (const vepi16 vec0, const vepi16 vec1) { return _mm512_mullo_epi16(vec0, vec1); }
inline vepi16 vec_srli_epi16  (const vepi16 vec, const int shift) { return _mm512_srli_epi16(vec, shift); }
inline vepi32 vec_dpwssd_epi32(const vepi32 sum, const vepi16 vec0, const vepi16 vec1) {
    #if defined(USE_VNNI512)
    return _mm512_dpwssd_epi32(sum, vec0, vec1);
    #else
    return _mm512_add_epi32(sum, _mm512_madd_epi16(vec0, vec1));
    #endif
}

inline vps32 vec_haddx8_cvtepi32_ps(const vepi32 *vecs) {
    auto m512i_to_m256i = [](const vepi32 vec) {
        const __m256i upper256 = _mm512_extracti64x4_epi64(vec, 1); // same as _mm512_extracti32x8_epi32, but doesn't require AVX512DQ
        const __m256i lower256 = _mm512_castsi512_si256(vec);
        return _mm256_add_epi32(upper256, lower256);
    };
    const __m256i sum01 = _mm256_hadd_epi32(m512i_to_m256i(vecs[0]), m512i_to_m256i(vecs[1]));
    const __m256i sum23 = _mm256_hadd_epi32(m512i_to_m256i(vecs[2]), m512i_to_m256i(vecs[3]));
    const __m256i sum45 = _mm256_hadd_epi32(m512i_to_m256i(vecs[4]), m512i_to_m256i(vecs[5]));
    const __m256i sum67 = _mm256_hadd_epi32(m512i_to_m256i(vecs[6]), m512i_to_m256i(vecs[7]));

    const __m256i sum0123 = _mm256_hadd_epi32(sum01, sum23);
    const __m256i sum4567 = _mm256_hadd_epi32(sum45, sum67);

    const __m128i sumALow = _mm256_castsi256_si128(sum0123);
    const __m128i sumAHi  = _mm256_extracti128_si256(sum0123, 1);
    const __m128i sumA    = _mm_add_epi32(sumALow, sumAHi);

    const __m128i sumBLow = _mm256_castsi256_si128(sum4567);
    const __m128i sumBHi  = _mm256_extracti128_si256(sum4567, 1);
    const __m128i sumB    = _mm_add_epi32(sumBLow, sumBHi);

    const __m256i sumAB   = _mm256_inserti128_si256(_mm256_castsi128_si256(sumA), sumB, 1);
    return _mm256_cvtepi32_ps(sumAB);
}

inline vps32 vec_zero_ps () { return _mm256_setzero_ps(); }
inline vps32 vec_set1_ps (const float n) { return _mm256_set1_ps(n); }
inline vps32 vec_load_ps (const float *src) { return _mm256_load_ps(src); }
inline void  vec_store_ps(float *dst, const vps32 vec) { return _mm256_store_ps(dst, vec); }
inline vps32 vec_add_ps  (const vps32 vec0, const vps32 vec1) { return _mm256_add_ps(vec0, vec1); }
inline vps32 vec_mul_ps  (const vps32 vec0, const vps32 vec1) { return _mm256_mul_ps(vec0, vec1); }
inline vps32 vec_div_ps  (const vps32 vec0, const vps32 vec1) { return _mm256_div_ps(vec0, vec1); }
inline vps32 vec_min_ps  (const vps32 vec0, const vps32 vec1) { return _mm256_min_ps(vec0, vec1); }
inline vps32 vec_max_ps  (const vps32 vec0, const vps32 vec1) { return _mm256_max_ps(vec0, vec1); }
inline vps32 vec_mul_add_ps(const vps32 vec0, const vps32 vec1, const vps32 vec2) { return _mm256_fmadd_ps(vec0, vec1, vec2); }
inline vps32 vec_hadd_psx8 (const vps32 *vecs) {
    const vps32 sum01 = _mm256_hadd_ps(vecs[0], vecs[1]);
    const vps32 sum23 = _mm256_hadd_ps(vecs[2], vecs[3]);
    const vps32 sum45 = _mm256_hadd_ps(vecs[4], vecs[5]);
    const vps32 sum67 = _mm256_hadd_ps(vecs[6], vecs[7]);

    const vps32 sum0123 = _mm256_hadd_ps(sum01, sum23);
    const vps32 sum4567 = _mm256_hadd_ps(sum45, sum67);

    const vps32 sumA = _mm256_permute2f128_ps(sum0123, sum4567, 0x20);
    const vps32 sumB = _mm256_permute2f128_ps(sum0123, sum4567, 0x31);
    return _mm256_add_ps(sumA, sumB);
}

inline float vec_reduce_add_ps(const vps32 vec) {
    const __m128 upper_128 = _mm256_extractf128_ps(vec, 1);
    const __m128 lower_128 = _mm256_castps256_ps128(vec);
    const __m128 sum_128 = _mm_add_ps(upper_128, lower_128);

    const __m128 upper_64 = _mm_movehl_ps(sum_128, sum_128);
    const __m128 sum_64 = _mm_add_ps(upper_64, sum_128);

    const __m128 upper_32 = _mm_shuffle_ps(sum_64, sum_64, 1);
    const __m128 sum_32 = _mm_add_ss(upper_32, sum_64);

    return _mm_cvtss_f32(sum_32);
}

#elif defined(USE_AVX2)
using vepi16 = __m256i;
using vepi32 = __m256i;
using vps32  = __m256;

inline vepi16 vec_zero_epi16() { return _mm256_setzero_si256(); }
inline vepi32 vec_zero_epi32() { return _mm256_setzero_si256(); }
inline vepi16 vec_set1_epi16  (const int16_t n) { return _mm256_set1_epi16(n); }
inline vepi16 vec_load_epi    (const vepi16 *src) { return _mm256_load_si256(src); }
inline vepi16 vec_max_epi16   (const vepi16 vec0, const vepi16 vec1) { return _mm256_max_epi16(vec0, vec1); }
inline vepi16 vec_min_epi16   (const vepi16 vec0, const vepi16 vec1) { return _mm256_min_epi16(vec0, vec1); }
inline vepi16 vec_mullo_epi16 (const vepi16 vec0, const vepi16 vec1) { return _mm256_mullo_epi16(vec0, vec1); }
inline vepi16 vec_srli_epi16  (const vepi16 vec, const int shift) { return _mm256_srli_epi16(vec, shift); }
inline vepi32 vec_dpwssd_epi32(const vepi32 sum, const vepi16 vec0, const vepi16 vec1) {
    return _mm256_add_epi32(sum, _mm256_madd_epi16(vec0, vec1));
}

inline vps32 vec_haddx8_cvtepi32_ps(const vepi32 *vecs) {
    const __m256i sum01 = _mm256_hadd_epi32(vecs[0], vecs[1]);
    const __m256i sum23 = _mm256_hadd_epi32(vecs[2], vecs[3]);
    const __m256i sum45 = _mm256_hadd_epi32(vecs[4], vecs[5]);
    const __m256i sum67 = _mm256_hadd_epi32(vecs[6], vecs[7]);

    const __m256i sum0123 = _mm256_hadd_epi32(sum01, sum23);
    const __m256i sum4567 = _mm256_hadd_epi32(sum45, sum67);

    const __m128i sumALow = _mm256_castsi256_si128(sum0123);
    const __m128i sumAHi  = _mm256_extracti128_si256(sum0123, 1);
    const __m128i sumA    = _mm_add_epi32(sumALow, sumAHi);

    const __m128i sumBLow = _mm256_castsi256_si128(sum4567);
    const __m128i sumBHi  = _mm256_extracti128_si256(sum4567, 1);
    const __m128i sumB    = _mm_add_epi32(sumBLow, sumBHi);

    const __m256i sumAB   = _mm256_inserti128_si256(_mm256_castsi128_si256(sumA), sumB, 1);
    return _mm256_cvtepi32_ps(sumAB);
}

inline vps32 vec_zero_ps () { return _mm256_setzero_ps(); }
inline vps32 vec_set1_ps (const float n) { return _mm256_set1_ps(n); }
inline vps32 vec_load_ps (const float *src) { return _mm256_load_ps(src); }
inline void  vec_store_ps(float *dst, const vps32 vec) { return _mm256_store_ps(dst, vec); }
inline vps32 vec_add_ps  (const vps32 vec0, const vps32 vec1) { return _mm256_add_ps(vec0, vec1); }
inline vps32 vec_mul_ps  (const vps32 vec0, const vps32 vec1) { return _mm256_mul_ps(vec0, vec1); }
inline vps32 vec_div_ps  (const vps32 vec0, const vps32 vec1) { return _mm256_div_ps(vec0, vec1); }
inline vps32 vec_min_ps  (const vps32 vec0, const vps32 vec1) { return _mm256_min_ps(vec0, vec1); }
inline vps32 vec_max_ps  (const vps32 vec0, const vps32 vec1) { return _mm256_max_ps(vec0, vec1); }
inline vps32 vec_mul_add_ps(const vps32 vec0, const vps32 vec1, const vps32 vec2) { return _mm256_fmadd_ps(vec0, vec1, vec2); }
inline vps32 vec_hadd_psx8 (const vps32 *vecs) {
    const vps32 sum01 = _mm256_hadd_ps(vecs[0], vecs[1]);
    const vps32 sum23 = _mm256_hadd_ps(vecs[2], vecs[3]);
    const vps32 sum45 = _mm256_hadd_ps(vecs[4], vecs[5]);
    const vps32 sum67 = _mm256_hadd_ps(vecs[6], vecs[7]);

    const vps32 sum0123 = _mm256_hadd_ps(sum01, sum23);
    const vps32 sum4567 = _mm256_hadd_ps(sum45, sum67);

    const vps32 sumA = _mm256_permute2f128_ps(sum0123, sum4567, 0x20);
    const vps32 sumB = _mm256_permute2f128_ps(sum0123, sum4567, 0x31);
    return _mm256_add_ps(sumA, sumB);
}

inline float vec_reduce_add_ps(const vps32 vec) {
    const __m128 upper_128 = _mm256_extractf128_ps(vec, 1);
    const __m128 lower_128 = _mm256_castps256_ps128(vec);
    const __m128 sum_128 = _mm_add_ps(upper_128, lower_128);

    const __m128 upper_64 = _mm_movehl_ps(sum_128, sum_128);
    const __m128 sum_64 = _mm_add_ps(upper_64, sum_128);

    const __m128 upper_32 = _mm_shuffle_ps(sum_64, sum_64, 1);
    const __m128 sum_32 = _mm_add_ss(upper_32, sum_64);

    return _mm_cvtss_f32(sum_32);
}
#endif