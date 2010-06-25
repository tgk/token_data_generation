(ns core
  (:gen-class)  
  (:import 
   [java.io File FileInputStream]
   [org.openscience.cdk DefaultChemObjectBuilder]
   [org.openscience.cdk.io.iterator IteratingMDLReader]
   [org.openscience.cdk.fingerprint Fingerprinter])
  (:use clojure.contrib.duck-streams)
  (:require clojure.contrib.str-utils2))

(defn read-sdf-file [filename]
  (let [file (File. filename)
        stream (FileInputStream. file)
        reader (IteratingMDLReader. stream 
				    (DefaultChemObjectBuilder/getInstance))]
    (iterator-seq reader)))

(defn fingerprint [fingerprinter molecule]
  (let [bitset (.getFingerprint fingerprinter molecule)]
    (filter #(.get bitset %) (range (.getSize fingerprinter)))))

(defn fingerprint-string [bits-set]
  (apply str (interpose "," (map str bits-set))))

(defn sparse-fingerprints [size inputfile outputfile]
  (let [size (Integer/parseInt size)
	fingerprinter (Fingerprinter. size)]
    (println "Using size" size "for hashing")
    (spit 
     outputfile
     (str
      (->> inputfile
	   read-sdf-file
	   (map (partial fingerprint fingerprinter))
	   (map fingerprint-string)
	   (interpose "\n")
	   (apply str))
      "\n"))))

(defn different-sizes [max-i inputfile]
  (let [molecule (first (read-sdf-file inputfile))
	max-i (Integer/parseInt max-i)]
    (doseq [i (range (inc max-i))]
      (let [size (Math/pow 2 i)
	    fingerprinter (Fingerprinter. size)]
	(println i size (count (fingerprint fingerprinter molecule)))))))
      
(def -main sparse-fingerprints)
