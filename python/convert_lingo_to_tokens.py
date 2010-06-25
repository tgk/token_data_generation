#!/usr/bin/env python
import sys
import stringgrams
import unique_grams

filename = sys.argv[1]
q = int(sys.argv[2])

uniqueGrams = unique_grams.UniqueGrams()

for line in open(filename, "r"):
	grams = stringgrams.extract_grams(line, q)
	grams = uniqueGrams.get_uids_from_grams(grams)
	grams.sort()
	grams = map(str, grams)
	print ",".join(grams)
