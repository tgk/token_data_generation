#!/usr/bin/env python
import sys
import unique_grams

filename = sys.argv[1]
uniqueGrams = unique_grams.UniqueGrams()

for line in open(filename):
    line = line.rstrip()
    tokens = line.split(",")
    ids = uniqueGrams.get_uids_from_grams(tokens)
    ids = map(int, ids)
    ids.sort()
    ids = map(str, ids)
    print ",".join(ids)

