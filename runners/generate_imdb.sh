#!/usr/bin/env sh

INFILE="downloaded/imdb.txt"
BASEDIR=".."
OUTDIR="output/imdb"

mkdir -p ${OUTDIR}

cd python
for q in {1..4}; do
    echo "Generating IMDB tokens for Q=${q}"
    OUTFILE="${OUTDIR}/imdb.q${q}.tokens"
    python convert_imdb_to_tokens.py \
	${BASEDIR}/${INFILE} ${q} > ${BASEDIR}/${OUTFILE}
    echo "done"
done