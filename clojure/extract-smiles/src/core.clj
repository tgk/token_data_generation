(ns core
  (:import 
   [java.io File FileInputStream]
   [org.openscience.cdk DefaultChemObjectBuilder]
   [org.openscience.cdk.io.iterator IteratingMDLReader]
   [org.openscience.cdk.smiles SmilesParser SmilesGenerator])
  (:use clojure.contrib.duck-streams)
  (:require [clojure.contrib.str-utils2 :as str-utils]))

(defn read-sdf-file [filename]
  (let [file (File. filename)
        stream (FileInputStream. file)
        reader (IteratingMDLReader. stream 
				    (DefaultChemObjectBuilder/getInstance))]
    (iterator-seq reader)))

(def smiles-generator (new SmilesGenerator))
(defn smiles-from-molecule [molecule] 
  (str (.createSMILES smiles-generator molecule) "\n"))

(defn replace-numbers-with-zero [s]
  (str-utils/replace s #"[0-9]+" "0"))
 
(defn replace-twochar-atoms [s]
  (-> s
      (str-utils/replace "Cl" "L")
      (str-utils/replace "Br" "R")))

(defn lingo [molecule]
  (-> molecule
      smiles-from-molecule
      replace-numbers-with-zero
      replace-twochar-atoms))

(defn handle-molecule-file [input-file output-file]
  (write-lines 
   output-file
   (map lingo (read-sdf-file input-file))))

(defn handle-SMILES-file [input-file output-file]
  (->> input-file 
       read-lines
       rest
       (map replace-numbers-with-zero)
       (map replace-twochar-atoms)
       (write-lines output-file)))

(defn generate-SMILES [input-file output-file]
  (spit
   output-file
   (apply str
	  (map smiles-from-molecule (read-sdf-file input-file)))))