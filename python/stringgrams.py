#!/usr/bin/env python

def extract_grams(s, q):
  grams = []
  for i in range(len(s) - q + 1):
    grams.append(s[i:i+q])
  return grams

def extract_padded_grams(s, q, left_pad = '$', right_pad = '#'):
  padded = (left_pad * (q - 1)) + s + (right_pad * (q - 1))
  grams = []
  for i in range(len(padded) - (q - 1)):
    grams.append(padded[i:i+q])
  return grams
  
if __name__ == "__main__":
  def test_s(s, q):
    print s
    print extract_grams(s, q)
    print
    
  test_s("test", 1)
  test_s("test", 2)
  test_s("test", 3)

  test_s("abbabba", 2)
  
