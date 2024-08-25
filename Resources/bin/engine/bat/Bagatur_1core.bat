
echo off

REM Sets path to libraries
set BIN_PATH=./bin/

REM Sets the path to jar files containing the compiled java code of Bagatur engine
set JARS_PATH=%BIN_PATH%BagaturEngine.jar;

REM Sets the memory (in megabytes) which the WHOLE java process will use.
REM One significant part of this memory (up to 50%) will be used for Transposition Table.
set PROCESS_MEMORY=4G

echo on

java.exe -Xmx%PROCESS_MEMORY% -Djava.library.path=%BIN_PATH% -cp %JARS_PATH% bagaturchess.uci.run.BagaturMain_1Core

