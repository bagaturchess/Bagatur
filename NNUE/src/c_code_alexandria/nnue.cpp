#include "nnue.h"
//#include "simd.h"
#include "position.h"
#include <algorithm>
#include <cmath>
#include <cstdio>
#include <cstring>
#include <iostream>
//#include "incbin.h"

// Macro to embed the default efficiently updatable neural network (NNUE) file
// data in the engine binary (using incbin.h, by Dale Weiler).
// This macro invocation will declare the following three variables
//     const unsigned char        gEVALData[];  // a pointer to the embedded data
//     const unsigned char *const gEVALEnd;     // a marker to the end
//     const unsigned int         gEVALSize;    // the size of the embedded file
// Note that this does not work in Microsoft Visual Studio.
//#if !defined(_MSC_VER)
//INCBIN(EVAL, EVALFILE);
//#else
const unsigned char gEVALData[1] = {};
const unsigned char* const gEVALEnd = &gEVALData[1];
const unsigned int gEVALSize = 1;
//#endif

Network net;

// Thanks to Disservin for having me look at his code and Luecx for the
// invaluable help and the immense patience

void NNUE::init(const char* file) {

    UnquantisedNetwork *unquantisedNet = static_cast<UnquantisedNetwork*>(malloc(sizeof(UnquantisedNetwork)));

    // open the nn file
    FILE* nn = fopen(file, "rb");

    // if it's not invalid read the config values from it
    if (nn) {
        // initialize an accumulator for every input of the second layer
        size_t read = 0;
        const size_t fileSize = sizeof(UnquantisedNetwork);
        const size_t objectsExpected = fileSize / sizeof(float);

        read += fread(unquantisedNet->FTWeights, sizeof(float), NUM_INPUTS * L1_SIZE, nn);
        read += fread(unquantisedNet->FTBiases, sizeof(float), L1_SIZE, nn);

        read += fread(unquantisedNet->L1Weights, sizeof(float), OUTPUT_BUCKETS * 2 * L1_SIZE * L2_SIZE, nn);
        read += fread(unquantisedNet->L1Biases, sizeof(float), OUTPUT_BUCKETS * L2_SIZE, nn);

        read += fread(unquantisedNet->L2Weights, sizeof(float), OUTPUT_BUCKETS * L2_SIZE * L3_SIZE, nn);
        read += fread(unquantisedNet->L2Biases, sizeof(float), OUTPUT_BUCKETS * L3_SIZE, nn);

        read += fread(unquantisedNet->L3Weights, sizeof(float), OUTPUT_BUCKETS * L3_SIZE, nn);
        read += fread(unquantisedNet->L3Biases, sizeof(float), OUTPUT_BUCKETS, nn);

        if (read != objectsExpected) {
            std::cout << "Error loading the net, aborting ";
            std::cout << "Expected " << objectsExpected << " floats, got " << read << "\n";
            exit(1);
        }

        // after reading the config we can close the file
        fclose(nn);

    }/* else {
        // if we don't find the nnue file we use the net embedded in the exe
        uint64_t memoryIndex = 0;
        std::memcpy(unquantisedNet->FTWeights, &gEVALData[memoryIndex], sizeof(float) * NUM_INPUTS * L1_SIZE);
        memoryIndex += sizeof(float) * NUM_INPUTS * L1_SIZE;
        std::memcpy(unquantisedNet->FTBiases, &gEVALData[memoryIndex], sizeof(float) * L1_SIZE);
        memoryIndex += sizeof(float) * L1_SIZE;

        std::memcpy(unquantisedNet->L1Weights, &gEVALData[memoryIndex], sizeof(float) * OUTPUT_BUCKETS * 2 * L1_SIZE * L2_SIZE);
        memoryIndex += sizeof(float) * OUTPUT_BUCKETS * 2 * L1_SIZE * L2_SIZE;
        std::memcpy(unquantisedNet->L1Biases, &gEVALData[memoryIndex], sizeof(float) * OUTPUT_BUCKETS * L2_SIZE);
        memoryIndex += sizeof(float) * OUTPUT_BUCKETS * L2_SIZE;

        std::memcpy(unquantisedNet->L2Weights, &gEVALData[memoryIndex], sizeof(float) * OUTPUT_BUCKETS * L2_SIZE * L3_SIZE);
        memoryIndex += sizeof(float) * OUTPUT_BUCKETS * L2_SIZE * L3_SIZE;
        std::memcpy(unquantisedNet->L2Biases, &gEVALData[memoryIndex], sizeof(float) * OUTPUT_BUCKETS * L3_SIZE);
        memoryIndex += sizeof(float) * OUTPUT_BUCKETS * L3_SIZE;

        std::memcpy(unquantisedNet->L3Weights, &gEVALData[memoryIndex], sizeof(float) * OUTPUT_BUCKETS * L3_SIZE);
        memoryIndex += sizeof(float) * OUTPUT_BUCKETS * L3_SIZE;
        std::memcpy(unquantisedNet->L3Biases, &gEVALData[memoryIndex], sizeof(float) * OUTPUT_BUCKETS);
        memoryIndex += sizeof(float) * OUTPUT_BUCKETS;
    }*/

    // Quantise FT Weights
    for (int i = 0; i < NUM_INPUTS * L1_SIZE; ++i)
        net.FTWeights[i] = static_cast<int16_t>(std::round(unquantisedNet->FTWeights[i] * FT_QUANT));

    // Quantise FT Biases
    for (int i = 0; i < L1_SIZE; ++i)
        net.FTBiases[i] = static_cast<int16_t>(std::round(unquantisedNet->FTBiases[i] * FT_QUANT));

    // Transpose L1, L2 and L3 weights and biases
    for (int bucket = 0; bucket < OUTPUT_BUCKETS; ++bucket) {

        // Quantise L1 Weights
        #if defined(USE_SIMD)
        for (int i = 0; i < 2 * L1_SIZE / L1_CHUNK_SIZE; ++i)
            for (int j = 0; j < L2_SIZE; ++j)
                for (int k = 0; k < L1_CHUNK_SIZE; ++k)
                    net.L1Weights[bucket][  i * L1_CHUNK_SIZE * L2_SIZE
                                          + j * L1_CHUNK_SIZE
                                          + k] = static_cast<int16_t>(std::round(unquantisedNet->L1Weights[i * L1_CHUNK_SIZE + k][bucket][j] * L1_QUANT));
        #else
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < L2_SIZE; ++j)
                for (int k = 0; k < L1_SIZE; ++k)
                    net.L1Weights[bucket][  i * L1_SIZE * L2_SIZE
                                          + j * L1_SIZE
                                          + k] = static_cast<int16_t>(std::round(unquantisedNet->L1Weights[i * L1_SIZE + k][bucket][j] * L1_QUANT));
        #endif

        // Quantise L1 Biases
        for (int i = 0; i < L2_SIZE; ++i)
            net.L1Biases[bucket][i] = unquantisedNet->L1Biases[bucket][i];

        // Quantise L2 Weights
        #if defined(USE_SIMD)
        for (int i = 0; i < L2_SIZE / L2_CHUNK_SIZE; ++i)
            for (int j = 0; j < L3_SIZE; ++j)
                for (int k = 0; k < L2_CHUNK_SIZE; ++k)
                    net.L2Weights[bucket][  i * L2_CHUNK_SIZE * L3_SIZE
                                          + j * L2_CHUNK_SIZE
                                          + k] = unquantisedNet->L2Weights[i * L2_CHUNK_SIZE + k][bucket][j];
        #else
        for (int i = 0; i < L2_SIZE; ++i)
            for (int j = 0; j < L3_SIZE; ++j)
                net.L2Weights[bucket][j * L2_SIZE + i] = unquantisedNet->L2Weights[i][bucket][j];
        #endif

        // Quantise L2 Biases
        for (int i = 0; i < L3_SIZE; ++i)
            net.L2Biases[bucket][i] = unquantisedNet->L2Biases[bucket][i];

        // Quantise L3 Weights
        for (int i = 0; i < L3_SIZE; ++i)
            net.L3Weights[bucket][i] = unquantisedNet->L3Weights[i][bucket];

        // Quantise L3 Biases
        net.L3Biases[bucket] = unquantisedNet->L3Biases[bucket];
    }

    free(unquantisedNet);
}

void NNUE::accumulate(NNUE::Accumulator& board_accumulator, Position* pos) {
    for (int i = 0; i < L1_SIZE; i++) {
        board_accumulator.values[0][i] = net.FTBiases[i];
        board_accumulator.values[1][i] = net.FTBiases[i];
    }

    for (int i = 0; i < 64; i++) {
        bool input = pos->pieces[i] != EMPTY;
        if (!input) continue;
        auto [whiteIdx, blackIdx] = GetIndex(pos->pieces[i], i);
        auto whiteAdd = &net.FTWeights[whiteIdx * L1_SIZE];
        auto blackAdd = &net.FTWeights[blackIdx * L1_SIZE];
        for (int j = 0; j < L1_SIZE; j++) {
            board_accumulator.values[0][j] += whiteAdd[j];
        }
        for (int j = 0; j < L1_SIZE; j++) {
            board_accumulator.values[1][j] += blackAdd[j];
        }
    }
}

void NNUE::update(NNUE::Accumulator *acc) {

    int adds = acc->NNUEAdd.size();
    int subs = acc->NNUESub.size();

    if (adds == 0 && subs == 0)
        return;

    if (!(acc - 1)->NNUEAdd.empty() && !(acc - 1)->NNUESub.empty())
        update(acc - 1);

    // Quiets
    if (adds == 1 && subs == 1) {
        addSub(acc, acc - 1, acc->NNUEAdd[0], acc->NNUESub[0]);
    }
    // Captures
    else if (adds == 1 && subs == 2) {
        addSubSub(acc, acc - 1, acc->NNUEAdd[0], acc->NNUESub[0], acc->NNUESub[1]);
    }
    // Castling
    else {
        addSub(acc, acc - 1, acc->NNUEAdd[0], acc->NNUESub[0]);
        addSub(acc, acc, acc->NNUEAdd[1], acc->NNUESub[1]);
        // Note that for second addSub, we put acc instead of acc - 1 because we are updating on top of
        // the half-updated accumulator
    }
    // Reset the add and sub vectors
    acc->NNUEAdd.clear();
    acc->NNUESub.clear();
}

void NNUE::addSub(NNUE::Accumulator *new_acc, NNUE::Accumulator *prev_acc, NNUEIndices add, NNUEIndices sub) {
    auto [whiteAddIdx, blackAddIdx] = add;
    auto [whiteSubIdx, blackSubIdx] = sub;
    auto whiteAdd = &net.FTWeights[whiteAddIdx * L1_SIZE];
    auto whiteSub = &net.FTWeights[whiteSubIdx * L1_SIZE];
    for (int i = 0; i < L1_SIZE; i++) {
        new_acc->values[0][i] = prev_acc->values[0][i] - whiteSub[i] + whiteAdd[i];
    }
    auto blackAdd = &net.FTWeights[blackAddIdx * L1_SIZE];
    auto blackSub = &net.FTWeights[blackSubIdx * L1_SIZE];
    for (int i = 0; i < L1_SIZE; i++) {
        new_acc->values[1][i] = prev_acc->values[1][i] - blackSub[i] + blackAdd[i];
    }
}

void NNUE::addSubSub(NNUE::Accumulator *new_acc, NNUE::Accumulator *prev_acc, NNUEIndices add, NNUEIndices sub1, NNUEIndices sub2) {

    auto [whiteAddIdx, blackAddIdx] = add;
    auto [whiteSubIdx1, blackSubIdx1] = sub1;
    auto [whiteSubIdx2, blackSubIdx2] = sub2;

    auto whiteAdd = &net.FTWeights[whiteAddIdx * L1_SIZE];
    auto whiteSub1 = &net.FTWeights[whiteSubIdx1 * L1_SIZE];
    auto whiteSub2 = &net.FTWeights[whiteSubIdx2 * L1_SIZE];
    for (int i = 0; i < L1_SIZE; i++) {
        new_acc->values[0][i] = prev_acc->values[0][i] - whiteSub1[i] - whiteSub2[i] + whiteAdd[i];
    }
    auto blackAdd = &net.FTWeights[blackAddIdx * L1_SIZE];
    auto blackSub1 = &net.FTWeights[blackSubIdx1 * L1_SIZE];
    auto blackSub2 = &net.FTWeights[blackSubIdx2 * L1_SIZE];
    for (int i = 0; i < L1_SIZE; i++) {
        new_acc->values[1][i] = prev_acc->values[1][i] - blackSub1[i] - blackSub2[i] + blackAdd[i];
    }
}

void NNUE::ActivateFTAndPropagateL1(const int16_t *us, const int16_t *them, const int16_t *weights, const float *biases, float *output) {
    #if defined(USE_SIMD)
    vepi32 sums[L2_SIZE] = {};
    const vepi16 Zero = vec_zero_epi16();
    const vepi16 One  = vec_set1_epi16(FT_QUANT);
    int weightOffset = 0;
    for (const int16_t *acc : {us, them}) {
        for (int i = 0; i < L1_SIZE; i += L1_CHUNK_SIZE) {
            // Activate feature transformers
            const vepi16 input   = vec_load_epi(reinterpret_cast<const vepi16*>(&acc[i]));
            const vepi16 clipped = vec_min_epi16(vec_max_epi16(input, Zero), One);
            const vepi16 squared = vec_srli_epi16(vec_mullo_epi16(clipped, clipped), FT_SHIFT);

            // Affine transform for L1
            const vepi16 *weight = reinterpret_cast<const vepi16*>(&weights[i * L2_SIZE + weightOffset]);
            for (int out = 0; out < L2_SIZE; ++out)
                sums[out] = vec_dpwssd_epi32(sums[out], squared, weight[out]);
        }

        weightOffset += L1_SIZE * L2_SIZE;
    }

    for (int i = 0; i < L2_SIZE; i += L2_CHUNK_SIZE) {
        // Convert into floats, and activate L1
        const vps32 biasVec = vec_load_ps(&biases[i]);
        const vps32 sumDiv  = vec_set1_ps(float(FT_QUANT * FT_QUANT * L1_QUANT >> FT_SHIFT));
        const vps32 sumPs   = vec_add_ps(vec_div_ps(vec_haddx8_cvtepi32_ps(&sums[i]), sumDiv), biasVec);
        const vps32 zeroPs  = vec_zero_ps();
        const vps32 onePs   = vec_set1_ps(1.0f);
        const vps32 clipped = vec_min_ps(vec_max_ps(sumPs, zeroPs), onePs);
        const vps32 squared = vec_mul_ps(clipped, clipped);
        vec_store_ps(&output[i], squared);
    }

    #else
    int sums[L2_SIZE] = {};
    int weightOffset = 0;
    for (const int16_t *acc : {us, them}) {
        for (int i = 0; i < L1_SIZE; ++i) {
            // Activate FT
            const int16_t clipped = std::clamp(acc[i], int16_t(0), FT_QUANT);
            const int16_t squared = (clipped * clipped) >> FT_SHIFT;

            // Affine transform for L1
            for (int out = 0; out < L2_SIZE; ++out) {
                sums[out] += squared * weights[weightOffset + out * L1_SIZE + i];
            }
        }
        weightOffset += L1_SIZE * L2_SIZE;
    }

    for (int i = 0; i < L2_SIZE; ++i) {
        // Convert into floats and activate L1
        const float sumDiv  = float(FT_QUANT * FT_QUANT * L1_QUANT >> FT_SHIFT);
        const float clipped = std::clamp(float(sums[i]) / sumDiv + biases[i], 0.0f, 1.0f);
        output[i] = clipped * clipped;
    }
    #endif
}

void NNUE::PropagateL2(const float *inputs, const float *weights, const float *biases, float *output) {
    #if defined(USE_SIMD)
    vps32 sumVecs[L3_SIZE] = {};

    // Affine transform for L2
    for (int i = 0; i < L2_SIZE; i += L2_CHUNK_SIZE) {
        const vps32 *weightVecs = reinterpret_cast<const vps32*>(&weights[i * L3_SIZE]);
        const vps32 inputsVec   = vec_load_ps(&inputs[i]);
        for (int out = 0; out < L3_SIZE; ++out)
            sumVecs[out] = vec_mul_add_ps(inputsVec, weightVecs[out], sumVecs[out]);
    }

    // Activate L2
    for (int i = 0; i < L3_SIZE; i += L3_CHUNK_SIZE) {
        const vps32 biasVec = vec_load_ps(&biases[i]);
        const vps32 sum     = vec_add_ps(vec_hadd_psx8(&sumVecs[i]), biasVec);
        const vps32 Zero    = vec_set1_ps(0.0f);
        const vps32 One     = vec_set1_ps(1.0f);
        const vps32 clipped = vec_max_ps(vec_min_ps(sum, One), Zero);
        const vps32 squared = vec_mul_ps(clipped, clipped);
        vec_store_ps(&output[i], squared);
    }
    #else
    float sums[L3_SIZE];

    for (int i = 0; i < L3_SIZE; ++i)
        sums[i] = biases[i];

    // Affine transform for L2
    for (int i = 0; i < L2_SIZE; ++i) {
        for (int out = 0; out < L3_SIZE; ++out) {
            sums[out] += inputs[i] * weights[out * L2_SIZE + i];
        }
    }

    // Activate L2
    for (int i = 0; i < L3_SIZE; ++i) {
        const float clipped = std::clamp(sums[i], 0.0f, 1.0f);
        output[i] = clipped * clipped;
    }
    #endif
}

void NNUE::PropagateL3(const float *inputs, const float *weights, const float bias, float &output) {
    #if defined(USE_SIMD)
    vps32 sumVec = vec_set1_ps(0.0f);

    // Affine transform for L3
    for (int i = 0; i < L3_SIZE; i += L3_CHUNK_SIZE) {
        const vps32 weightVec = vec_load_ps(&weights[i]);
        const vps32 inputsVec = vec_load_ps(&inputs[i]);
        sumVec = vec_mul_add_ps(inputsVec, weightVec, sumVec);
    }
    output = bias + vec_reduce_add_ps(sumVec);
    #else
    float sum = bias;

    // Affine transform for L3
    for (int i = 0; i < L3_SIZE; ++i) {
        sum += inputs[i] * weights[i];
    }
    output = sum;
    #endif
}

// this function takes the net output for the current accumulators and returns the eval of the position
// according to the net
int32_t NNUE::output(const int16_t* us, const int16_t* them, const int outputBucket) {
    alignas(16) float L1Outputs[L2_SIZE];
    alignas(16) float L2Outputs[L3_SIZE];
    float L3Output;

    ActivateFTAndPropagateL1(us,
                             them,
                             net.L1Weights[outputBucket],
                             net.L1Biases[outputBucket],
                             L1Outputs);

    PropagateL2(L1Outputs,
                net.L2Weights[outputBucket],
                net.L2Biases[outputBucket],
                L2Outputs);

    PropagateL3(L2Outputs,
                net.L3Weights[outputBucket],
                net.L3Biases[outputBucket],
                L3Output);

    return L3Output * NET_SCALE;
}



/*int32_t NNUE::output(const NNUE::Accumulator& board_accumulator, const bool sideToMove, const int outputBucket) {

    alignas (16) float L1Outputs[L2_SIZE];
    alignas (16) float L2Outputs[L3_SIZE];
    float L3Output;

    const int16_t* us = board_accumulator.values[sideToMove].data();
    const int16_t* them = board_accumulator.values[sideToMove ^ 1].data();
    ActivateFTAndPropagateL1(us,
                             them,
                             net.L1Weights[outputBucket],
                             net.L1Biases[outputBucket],
                             L1Outputs);

    PropagateL2(L1Outputs,
                net.L2Weights[outputBucket],
                net.L2Biases[outputBucket],
                L2Outputs);

    PropagateL3(L2Outputs,
               net.L3Weights[outputBucket],
               net.L3Biases[outputBucket],
               L3Output);

    return L3Output * NET_SCALE;
}*/

NNUEIndices NNUE::GetIndex(const int piece, const int square) {
    constexpr std::size_t COLOR_STRIDE = 64 * 6;
    constexpr std::size_t PIECE_STRIDE = 64;
    int piecetype = GetPieceType(piece);
    int color = Color[piece];
    std::size_t whiteIdx = color * COLOR_STRIDE + piecetype * PIECE_STRIDE + (square ^ 0b111'000);
    std::size_t blackIdx = (1 ^ color) * COLOR_STRIDE + piecetype * PIECE_STRIDE + square;
    return {whiteIdx, blackIdx};
}

// Returns the piecetype of a piece
int GetPieceType(const int piece) {
    if (piece == EMPTY)
        return EMPTY;
    return PieceType[piece];
}