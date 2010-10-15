(defproject extract-smiles "1.0.0-SNAPSHOT"
  :description "Small program for generating canonical smiles."
  :repositories {"org.openscience" "https://maven.ch.cam.ac.uk/m2repo"}
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]
		 [org.openscience/cdk "1.3.4"]]
  :dev-dependencies [;[leiningen-run "0.2"]
		     [lein-run "1.0.0-SNAPSHOT"]
		     [swank-clojure "1.2.1"]])