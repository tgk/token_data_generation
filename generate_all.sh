#!/usr/bin/env bash

mkdir output
mkdir output/imdb
mkdir output/lingo
mkdir output/dense_fingerprints
mkdir output/sparse_fingerprints

mkdir downloaded

#Download data
sh runners/download_imdb.sh
sh runners/download_zinc.sh

#Generate data
sh runners/generate_imdb.sh
sh runners/generate_lingos.sh
sh runners/generate_fingerprints.sh 1024 output/dense_fingerprints dense.tokens
sh runners/generate_fingerprints.sh 524288 output/sparse_fingerprints sparse.tokens
