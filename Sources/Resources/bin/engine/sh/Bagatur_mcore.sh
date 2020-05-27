#!/bin/bash
# Goes to parent directory (the root of the distribution)
cd ..

# Sets path to libraries
export BIN_PATH=./bin/


# Sets the path to jar files containing the compiled java code of Bagatur engine
export JARS_PATH=".:${BIN_PATH}BagaturBoard.jar:${BIN_PATH}BagaturOpening.jar:${BIN_PATH}BagaturSearch.jar:${BIN_PATH}BagaturUCI.jar:${BIN_PATH}BagaturEngines.jar:${BIN_PATH}BagaturLearningAPI.jar:${BIN_PATH}BagaturEGTB.jar:${BIN_PATH}BagaturLearningImpl.jar:"


# Sets the memory (in megabytes) for the WHOLE java process.
# Only one part of this memory (up to 65%) will be used for Transposition Table.
export PROCESS_MEMORY=8192M

export ARGS=bagaturchess.engines.cfg.base.UCIConfig_BaseImpl
export ARGS="${ARGS} bagaturchess.search.impl.uci_adaptor.UCISearchAdaptorImpl_PonderingOpponentMove"
export ARGS="${ARGS} bagaturchess.engines.cfg.base.UCISearchAdaptorConfig_BaseImpl"
export ARGS="${ARGS} bagaturchess.search.impl.rootsearch.parallel.MTDParallelSearch_ThreadsImpl"
export ARGS="${ARGS} bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl_SMP_Threads"
export ARGS="${ARGS} bagaturchess.search.impl.alg.impl1.Search_PVS_NWS"
export ARGS="${ARGS} bagaturchess.engines.cfg.base.SearchConfigImpl_AB"
export ARGS="${ARGS} bagaturchess.learning.goldmiddle.impl4.cfg.BoardConfigImpl_V20"
export ARGS="${ARGS} bagaturchess.learning.goldmiddle.impl4.cfg.EvaluationConfig_V20"

# Executes the java process of the Bagatur engine with parallel search. It uses all CPU Cores.
# Sometimes, the engine performs a sequential search for depth below 6 because of performance reasons.
# The idea is to optimize the engine work for short time controls (1 move for 1 second or less). 
java -Xmx${PROCESS_MEMORY} -Djava.library.path=${BIN_PATH} -cp "${JARS_PATH}" bagaturchess.uci.run.Boot ${ARGS}

