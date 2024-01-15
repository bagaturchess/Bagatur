############################
# Target executable and files
############################
EXE = libnnueprobe.so
RM = rm -rf
OBJ = misc.o nnue.o
HPP = misc.h nnue.h

############################
# SIMD flags
#--------------
# Disable/Add flags as needed
############################
DEFINES =
DEFINES += -DIS_64BIT 
ifeq ($(COMP),$(filter $(COMP),gcc clang win icpc))
  DEFINES += -DUSE_AVX2 -mavx2
  DEFINES += -DUSE_SSE41 -msse4.1
  DEFINES += -DUSE_SSE3 -msse3
  DEFINES += -DUSE_SSE2 -msse2
  DEFINES += -DUSE_SSE -msse
endif

############################
# Compiler choice 
############################
DEBUG=0
COMP=gcc
STRIP=strip

ifeq ($(COMP),pgcc)
	CXX=pgc++
else ifeq ($(COMP),gcc)
	CXX=g++
else ifeq ($(COMP),clang)
	CXX=clang++
else ifeq ($(COMP),icpc)
	CXX=icpc
else ifeq ($(COMP),arm)
	CXX=arm-linux-androideabi-g++
	STRIP=arm-linux-androideabi-strip
else ifeq ($(COMP),win)
	CXX=x86_64-w64-mingw32-g++
	STRIP=x86_64-w64-mingw32-strip
endif

STRIP += $(EXE)

###########################
#  Compiler flags
###########################

LXXFLAGS = -lm

UNUSED =
ifeq ($(COMP),icpc)
else
    UNUSED += -Wno-unused-variable -Wno-unused-result
endif
ifeq ($(COMP),clang)
else
    UNUSED += -Wno-unused-but-set-variable -Wno-maybe-uninitialized
endif

ifeq ($(COMP),win)
    CXXFLAGS = -Wall -fstrict-aliasing -fno-exceptions -fno-rtti $(UNUSED) -std=c++11
    LXXFLAGS += -static
else ifeq ($(COMP),pgcc)
    CXXFLAGS = warn -Mvect=sse -c11
    LXXFLAGS += -ldl
else ifeq ($(COMP),icpc)
    CXXFLAGS  = -wd128 -wd981 -wd869 -wd2259 -wd383 -wd1418
    CXXFLAGS += -fstrict-aliasing -fno-exceptions -fno-rtti $(UNUSED) -std=c++11
    LXXFLAGS += -ldl
else
    CXXFLAGS = -Wall -fstrict-aliasing -fno-exceptions -fno-rtti $(UNUSED) -std=c++11
    LXXFLAGS += -ldl
endif

ifeq ($(COMP),arm)
     CXXFLAGS += -fPIE -pie
     LXXFLAGS += -fPIE -pie
endif

ifeq ($(DEBUG),3)
        ifeq ($(COMP),icpc)
                CXXFLAGS += -O2 -prof-gen
        else ifeq ($(COMP),pgcc)
                CXXFLAGS += -O2 -Mpfi
        else
                CXXFLAGS += -O2 -fprofile-generate
                LXXFLAGS += -lgcov
        endif
        STRIP=
else ifeq ($(DEBUG),2)
        ifeq ($(COMP),icpc)
                CXXFLAGS += -g -pg
        else
                CXXFLAGS += -g -pg
                LXXFLAGS += -g -pg
        endif
        STRIP=
else ifeq ($(DEBUG),1)
        ifeq ($(COMP),icpc)
                CXXFLAGS += -prof-use -fast -fomit-frame-pointer
        else ifeq ($(COMP),pgcc)
                CXXFLAGS += -Mpfo -fast noframe
        else
                CXXFLAGS += -fprofile-use -Ofast -fomit-frame-pointer
                LXXFLAGS += -lgcov
        endif
else
        ifeq ($(COMP),icpc)
                CXXFLAGS += -fast -fomit-frame-pointer
        else ifeq ($(COMP),pgcc)
                CXXFLAGS += -fast noframe
        else
                CXXFLAGS += -Ofast -fomit-frame-pointer
        endif
endif

######################
# Rules
######################

default:
	$(MAKE) $(EXE) strip

clean:
	$(RM) $(OBJ) $(EXE) core.* cluster.* *.gcda

strip:
	$(STRIP)

help:
	@echo ""
	@echo "1. make [DEBUG=n] [COMP=c]"
	@echo ""
	@echo "  n ="
	@echo "	0: Compile optimized binary (-03)"
	@echo "	1: Compile with profile guided optimization (PGO)"
	@echo "	2: Compile for deugging (default)"
	@echo "	3: Prepare for PGO"
	@echo ""
	@echo "  c ="
	@echo "	gcc    :  g++ compiler"
	@echo "	clang  :  clang compiler"
	@echo "	icpc   :  intel compiler"
	@echo "	pgcc   :  pgi compiler"
	@echo "	arm    :  arm android compiler"
	@echo "	win    :  windows cross compiler"
	@echo ""
	@echo "2. make clean - removes all files but source code"
	@echo "3. make strip - strips executable of debugging/profiling data"
	@echo ""

##############
# Dependencies
############## 

$(EXE): $(OBJ)
	$(CXX) -o $@ $(OBJ) $(LXXFLAGS) -shared 

%.o: %.cpp $(HPP)
	$(CXX) $(CXXFLAGS) $(DEFINES) -c -fPIC -o $@ $<
