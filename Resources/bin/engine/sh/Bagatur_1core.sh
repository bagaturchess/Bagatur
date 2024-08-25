#!/bin/bash

# Sets path to libraries
export BIN_PATH=./bin/

# Sets the path to jar files containing the compiled java code of Bagatur engine
export JARS_PATH=".:${BIN_PATH}BagaturEngine.jar:"

# Sets the memory (in megabytes) for the WHOLE java process.
# Only one part of this memory (up to 50%) will be used for Transposition Table.
export PROCESS_MEMORY=1024M

exec java -Xmx${PROCESS_MEMORY} -Djava.library.path=${BIN_PATH} -cp "${JARS_PATH}" bagaturchess.uci.run.BagaturMain_1Core

