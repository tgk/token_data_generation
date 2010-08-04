#!/usr/bin/env bash

OUTDIR="output/mayb4k_lingo"
mkdir -p ${OUTDIR}

INFILE="downloaded/mayb4k.gsm"
UNSPLITTET="${OUTDIR}/unsplitted"

cd clojure/extract-smiles
lein deps
time lein run \
    core handle-SMILES-file \
    ../../${INFILE} \
    ../../${UNSPLITTED}
cd ../..
echo "done"

cd python
q="4"
echo "Generating LINGO tokens for q=${q}"
SPLITTED="${OUTDIR}/mayb4k.q${q}.tokens"
time python convert_lingo_to_tokens.py ../${UNSPLITTED} ${q} > ../${SPLITTED}
cd ..
