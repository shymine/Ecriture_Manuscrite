#!/bin/bash
export LD_LIBRARY_PATH=$(pwd)/lib
bin/b-ligneancienread_PC data/LIGNEANCIEN_icdar.par $1 $2 -PM_overflow 80000000
