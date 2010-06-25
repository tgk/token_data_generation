(ns core
  (:gen-class)  
  (:import 
   [java.io File FileInputStream]
   [org.openscience.cdk DefaultChemObjectBuilder]
   [org.openscience.cdk.io.iterator IteratingMDLReader]
   [org.openscience.cdk.smiles SmilesParser SmilesGenerator])
  (:use clojure.contrib.duck-streams)
  (:require clojure.contrib.str-utils2)
  (:use clj-todo.todo))

(defn read-sdf-file [filename]
  (todo
   "I think this is a better way of writing the let statement"
   (comment let [reader (-> filename
			    File.
			    FileInputStream.
			    (IteratingMDLReader (DefaultChemObjectBuilder/getInstance)))]))
  (let [file (File. filename)
        stream (FileInputStream. file)
        reader (IteratingMDLReader. stream 
				    (DefaultChemObjectBuilder/getInstance))]
    (iterator-seq reader)))

(def smiles-generator (new SmilesGenerator))
(defn smiles-from-molecule [molecule] 
  (str
   (.createSMILES smiles-generator molecule) "\n"))

(defn replace-numbers-with-zero [s]
  (clojure.contrib.str-utils2/replace s #"[0-9]+" "0"))
 
(defn replace-twochar-atoms [s]
  (-> s
      (clojure.contrib.str-utils2/replace "Cl" "L")
      (clojure.contrib.str-utils2/replace "Br" "R")))

(defn lingo [molecule]
  (-> molecule
      smiles-from-molecule
      replace-numbers-with-zero
      replace-twochar-atoms))

(defn -main [inputfile outputfile]
  (spit 
   outputfile
   (apply str (map lingo (read-sdf-file inputfile)))))

(comment -main "../../data/lingo/zinc/headed", "test.fingerprints")

