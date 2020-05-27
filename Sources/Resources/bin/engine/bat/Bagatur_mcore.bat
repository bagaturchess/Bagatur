

REM Goes to parent directory (the root of the distribution)
cd ..

echo off

REM Sets path to libraries
set BIN_PATH=./bin/


REM Sets the path to jar files containing the compiled java code of Bagatur engine
set JARS_PATH=%BIN_PATH%egtbprobe.jar;%BIN_PATH%BagaturBoard.jar;%BIN_PATH%BagaturOpening.jar;%BIN_PATH%BagaturSearch.jar;%BIN_PATH%BagaturUCI.jar;%BIN_PATH%BagaturEngines.jar;%BIN_PATH%BagaturLearningAPI.jar;%BIN_PATH%BagaturEGTB.jar;%BIN_PATH%BagaturLearningImpl.jar;


REM Sets the memory (in megabytes) for the WHOLE java process.
REM Only one part of this memory (up to 65%) will be used for Transposition Table.
set PROCESS_MEMORY=8192M

set ARGS=bagaturchess.engines.cfg.base.UCIConfig_BaseImpl
set ARGS=%ARGS% bagaturchess.search.impl.uci_adaptor.UCISearchAdaptorImpl_PonderingOpponentMove
set ARGS=%ARGS% bagaturchess.engines.cfg.base.UCISearchAdaptorConfig_BaseImpl
set ARGS=%ARGS% bagaturchess.search.impl.rootsearch.parallel.MTDParallelSearch_ThreadsImpl
set ARGS=%ARGS% bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl_SMP_Threads
set ARGS=%ARGS% bagaturchess.search.impl.alg.impl1.Search_PVS_NWS
set ARGS=%ARGS% bagaturchess.engines.cfg.base.SearchConfigImpl_AB
set ARGS=%ARGS% bagaturchess.learning.goldmiddle.impl4.cfg.BoardConfigImpl_V20
set ARGS=%ARGS% bagaturchess.learning.goldmiddle.impl4.cfg.EvaluationConfig_V20

echo on

REM Executes the java process of the Bagatur engine with parallel search. It uses all CPU Cores.
REM Sometimes, the engine performs a sequential search for depth below 6 because of performance reasons.
REM The idea is to optimize the engine work for short time controls (1 move for 1 second or less). 
java.exe -Xmx%PROCESS_MEMORY% -Djava.library.path=%BIN_PATH% -cp %JARS_PATH% bagaturchess.uci.run.Boot %ARGS%

