#!/usr/bin/env bash

OUTDIR="output/lingo"
mkdir -p ${OUTDIR}

MAXQ=4

for i in {0..29}; do
    INFILE="downloaded/10_p0.${i}.sdf"
    UNSPLITTED="${OUTDIR}/unsplitted.${i}"
    
    echo $INFILE
    echo $UNSPLITTED

    echo "Generating LINGO strings"
    cd clojure/extract-smiles
    lein deps
    time lein run core generate-SMILES \
	../../${INFILE} \
	../../${UNSPLITTED}.smiles
    time lein run core handle-SMILES-file \
	../../${UNSPLITTED}.smiles \
	../../${UNSPLITTED}.pseudosmiles

    cd ../..
    echo "done"
done

cat ${OUTDIR}/unsplitted.*.smiles > ${OUTDIR}/unsplitted.smiles
cat ${OUTDIR}/unsplitted.*.pseudosmiles > ${OUTDIR}/unsplitted.pseudosmiles
rm ${OUTDIR}/unsplitted.*.smiles
rm ${OUTDIR}/unsplitted.*.pseudosmiles

cd python
for q in {1..4}; do
    echo "Generating LINGO tokens for q=${q}"
    SPLITTED="${OUTDIR}/lingo.q${q}.tokens"
    time python convert_lingo_to_tokens.py \
	../${OUTDIR}/unsplitted.pseudosmiles \
	${q} > \
	../${SPLITTED}
    echo "done"
done
cd ..

rm ${OUTDIR}/unsplitted.pseudosmiles