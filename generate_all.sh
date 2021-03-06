#!/usr/bin/env bash

mkdir output
mkdir output/imdb
mkdir output/lingo
mkdir output/dense_fingerprints
mkdir output/sparse_fingerprints
mkdir output/mayb4k_lingo

mkdir downloaded

#Download data
bash runners/download_imdb.sh
bash runners/download_zinc.sh
bash runners/download_mayb4k.sh

#Generate data
bash runners/generate_imdb.sh
bash runners/generate_lingos.sh
bash runners/generate_dense_fingerprints.sh 1024 output/dense_fingerprints dense.tokens
bash runners/generate_fingerprints.sh output/sparse_fingerprints sparse.tokens
bash runners/generate_mayb4k_lingos.sh