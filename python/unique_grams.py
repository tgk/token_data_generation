#!/usr/bin/env python

class UniqueGrams:
  def __init__(self):
    self.uid_counter = 0
    self.gram_and_occurence_to_uid = {}
  def get_uid(self, gram, occurence):
    key = (gram, occurence)
    if key not in self.gram_and_occurence_to_uid:
      self.gram_and_occurence_to_uid[key] = self.uid_counter
      self.uid_counter += 1
    return self.gram_and_occurence_to_uid[key]
  def get_uids_from_grams(self, grams):
    occurences = {}
    uids = []
    for gram in grams:
      if gram not in occurences:
        occurences[gram] = 0
      uids.append(self.get_uid(gram, occurences[gram]))
      occurences[gram] += 1
    return uids
    
if __name__ == "__main__":
  uniqueGrams = UniqueGrams()
  print uniqueGrams.get_uids_from_grams([1, 2, 3, 4])
  print uniqueGrams.get_uids_from_grams([1, 2, 3, 5])
  print uniqueGrams.get_uids_from_grams([1, 2, 3, 1])

  print uniqueGrams.get_uids_from_grams("abba")
  print uniqueGrams.get_uids_from_grams("abbb")
      