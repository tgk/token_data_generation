#!/usr/bin/env sh

#524288 for sparse, 1024 for dense
SIZE=$1
OUTDIR=$2
OUTNAME=$3

mkdir -p ${OUTDIR}

for i in {0..29}; do
    INFILE="downloaded/10_p0.${i}.sdf"
    RAW="${OUTDIR}/raw.${i}.fingerprints"
    
    echo $INFILE
    echo $RAW

    echo "Generating fingerprints"
    cd clojure/extract-sparse-fingerprints
    lein deps
    time lein run \
	${SIZE} \
	../../${INFILE} \
	../../${RAW}
    cd ../..
    echo "done"
done

cat ${OUTDIR}/raw.*.fingerprints > ${OUTDIR}/raw.fingerprints

echo "Removing spaces from data"
cd python && \
    python remove_spacing.py \
    ../${OUTDIR}/raw.fingerprints > \
    ../${OUTDIR}/${OUTNAME}
cd ..
echo "done"

rm ${OUTDIR}/raw*