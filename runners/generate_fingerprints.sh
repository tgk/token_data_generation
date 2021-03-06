#!/usr/bin/env bash

OUTDIR=$1
OUTNAME=$2

mkdir -p ${OUTDIR}

for i in {0..29}; do
    INFILE="downloaded/10_p0.${i}.sdf"
    RAW="${OUTDIR}/raw.${i}.fingerprints"
    
    echo $INFILE
    echo $RAW

    echo "Generating fingerprints"
    cd java
    mkdir bin
    javac -cp .:../downloaded/cdk-1.2.5.jar -sourcepath . -d bin Fingerprinter.java
    java -cp bin:../downloaded/cdk-1.2.5.jar Fingerprinter ../${INFILE} > ../${RAW}

    cd ..
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

#rm ${OUTDIR}/raw*