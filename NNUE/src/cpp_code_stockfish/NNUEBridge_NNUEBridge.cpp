#include "NNUEBridge_NNUEBridge.h"
#include <iostream>

#include "probe.h"

using namespace Stockfish;

JNIEXPORT void JNICALL Java_NNUEBridge_NNUEBridge_init
(JNIEnv *env, jclass this_class, jstring bigNet, jstring smallNet) {

const char *big = env->GetStringUTFChars(bigNet, NULL);
const char *small = env->GetStringUTFChars(smallNet, NULL);

Probe::init(big, small);

env->ReleaseStringUTFChars(bigNet, big);
env->ReleaseStringUTFChars(smallNet, small);
}

JNIEXPORT jint JNICALL Java_NNUEBridge_NNUEBridge_evalFen
        (JNIEnv * env, jclass this_class, jstring javaFen) {
const char *fen = env->GetStringUTFChars(javaFen, NULL);

int eval = Probe::eval(fen);

env->ReleaseStringUTFChars(javaFen, fen);

return eval;
}

JNIEXPORT jint JNICALL Java_NNUEBridge_NNUEBridge_evalArray
        (JNIEnv * env, jclass this_class, jintArray pieceBoard, jint side, jint rule50) {
    jint *board = env->GetIntArrayElements(pieceBoard, NULL);
    bool c_side = side == 0;
    int c_rule50 = (int) rule50;

    int eval = Probe::eval((int*)board, c_side, c_rule50);

    env->ReleaseIntArrayElements(pieceBoard, board, 0);

    return eval;
}

JNIEXPORT jint JNICALL Java_NNUEBridge_NNUEBridge_fasterEvalArray
        (JNIEnv * env, jclass this_class, jintArray pieces, jintArray squares,
         jint pieceAmount, jint side, jint rule50) {
    jint *c_pieces = (jint*)env->GetPrimitiveArrayCritical(pieces, 0);
    jint *c_squares = (jint*)env->GetPrimitiveArrayCritical(squares, 0);
    int c_pieceAmount = (int) pieceAmount;
    bool c_side = side == 0;
    int c_rule50 = (int) rule50;

    int eval = Probe::eval((int*)c_pieces, (int*)c_squares, c_pieceAmount, c_side, c_rule50);

    env->ReleasePrimitiveArrayCritical(pieces, c_pieces, 0);
    env->ReleasePrimitiveArrayCritical(squares, c_squares, 0);

    return eval;
}