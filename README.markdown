# Token data generation

A small collection of scripts for generating token data for use in Tanimoto query experiments.
The data is gathered from molecular data (dense and sparse fingerprints, and lingos) and an actor name data set (q-grams).

To generate the data, clone the repository and run `generate_all.sh`. If something fails, the data can be generated again by running `clean.sh` and the `generate_all.sh`.

The setup requires

* wget
* bash
* lein
* python

A full download and generation of data will take some time.
Run main script as nohup so you can log out.

