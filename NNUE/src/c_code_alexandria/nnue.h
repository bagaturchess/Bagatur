#pragma once

#include <cstdint>
#include <array>
#include <vector>
#include "simd.h"

// Net arch: (768 -> L1_SIZE) x 2 -> (L2_SIZE -> L3_SIZE -> 1) x OUTPUT_BUCKETS
constexpr int NUM_INPUTS = 768;
constexpr int L1_SIZE = 1536;
constexpr int L2_SIZE = 8;
constexpr int L3_SIZE = 32;
constexpr int OUTPUT_BUCKETS = 8;

constexpr int16_t FT_QUANT  = 255;
constexpr int16_t FT_SHIFT  = 1;
constexpr int16_t L1_QUANT  = 256;
constexpr int NET_SCALE = 400;

#if defined(USE_SIMD)
constexpr int FT_CHUNK_SIZE = sizeof(vepi16) / sizeof(int16_t);
constexpr int L1_CHUNK_SIZE = sizeof(vepi16) / sizeof(int16_t);
constexpr int L2_CHUNK_SIZE = sizeof(vps32 ) / sizeof(float);
constexpr int L3_CHUNK_SIZE = sizeof(vps32 ) / sizeof(float);
#else
constexpr int FT_CHUNK_SIZE = 1;
constexpr int L1_CHUNK_SIZE = 1;
constexpr int L2_CHUNK_SIZE = 1;
constexpr int L3_CHUNK_SIZE = 1;
#endif

using NNUEIndices = std::pair<std::size_t, std::size_t>;

struct alignas(64) Network {
    int16_t FTWeights[NUM_INPUTS * L1_SIZE];
    int16_t FTBiases [L1_SIZE];
    int16_t L1Weights[OUTPUT_BUCKETS][2 * L1_SIZE * L2_SIZE];
    float   L1Biases [OUTPUT_BUCKETS][L2_SIZE];
    float   L2Weights[OUTPUT_BUCKETS][L2_SIZE * L3_SIZE];
    float   L2Biases [OUTPUT_BUCKETS][L3_SIZE];
    float   L3Weights[OUTPUT_BUCKETS][L3_SIZE];
    float   L3Biases [OUTPUT_BUCKETS];
};

struct UnquantisedNetwork {
    float FTWeights[NUM_INPUTS * L1_SIZE];
    float FTBiases [L1_SIZE];
    float L1Weights[2 * L1_SIZE][OUTPUT_BUCKETS][L2_SIZE];
    float L1Biases [OUTPUT_BUCKETS][L2_SIZE];
    float L2Weights[L2_SIZE][OUTPUT_BUCKETS][L3_SIZE];
    float L2Biases [OUTPUT_BUCKETS][L3_SIZE];
    float L3Weights[L3_SIZE][OUTPUT_BUCKETS];
    float L3Biases [OUTPUT_BUCKETS];
};

extern Network net;
struct Position;

class NNUE {
public:
    struct Accumulator {
        alignas(64) std::array<std::array<int16_t, L1_SIZE>, 2> values;
        std::vector<NNUEIndices> NNUEAdd = {};
        std::vector<NNUEIndices> NNUESub = {};

        void AppendAddIndex(NNUEIndices index) {
            NNUEAdd.emplace_back(index);
        }

        void AppendSubIndex(NNUEIndices index) {
            NNUESub.emplace_back(index);
        }
    };

    void init(const char *file);
    void accumulate(NNUE::Accumulator &board_accumulator, Position* pos);
    void update(NNUE::Accumulator *acc);
    void addSub(NNUE::Accumulator *new_acc, NNUE::Accumulator *prev_acc, NNUEIndices add, NNUEIndices sub);
    void addSubSub(NNUE::Accumulator *new_acc, NNUE::Accumulator *prev_acc, NNUEIndices add, NNUEIndices sub1, NNUEIndices sub2);
    void ActivateFTAndPropagateL1(const int16_t *us, const int16_t *them, const int16_t *weights, const float *biases, float *output);
    void PropagateL2(const float *inputs, const float *weights, const float *biases, float *output);
    void PropagateL3(const float *inputs, const float *weights, const float bias, float &output);
    [[nodiscard]] int32_t output(const int16_t* us, const int16_t* them, const int outputBucket);
    [[nodiscard]] NNUEIndices GetIndex(const int piece, const int square);
};