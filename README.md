## Introduction
This is a information retrival system used to index the trec-cds data, and do the training and search work. The system is mainly based on Lucene, and the project has contained the jar package of Lucene and some other tools.

## Environment
* java
* Linux
* shell

## System Structure
* source files
    + TransOfFormat : to transform to trec format
    + indexing.Indexer : to index files
    + trec.SearchTopics
        + train the BM25 params
        + search topics
* lib
    + lucene jar packages
    + dom4j
* bin
    + java compiled class files
* data directory
    + pmc-text : store the extracted source trec files
    + index : store index files
    + sample_evavl.pl : used to evaluate
    + others
        + topics 
        + qrels 
        + some tmp files
* executable shell script
    + compile.sh : to compile java source files
    + parse.sh : to parse the file to trec format
    + indexing.sh : to index files
    + search.sh : to search the topics and generate the result file

## Realization
* transform files format
    + use TransOfFormat tools to transform the files to regular trec files
* index
    + use lucene to index the files, and save index to ../data/index
* train params
    + traverse b from [b1, b2] and k1 from [k', k'']
    + search the topics14.xml, generate a tmp file
    + use perl sample_eval.pl to evaluate the tmp file, and compare the NDCG value
    + choose the best k1 and b
* search topics15
    + use the best k1 and b to do the search

## Main java class
* tool TransOfFormat
* indexing.Indexer.java
* trec.SearchTopics.java

## Run
> one-key run is 
```
bash run.sh
```
and can be divided to several parts:
* compile
    + `bash compile.sh`
* parse
    + `bash parse.sh`
* index
    + `bash index.sh`
* search
    + `bash search.sh`

## Technologies & Principles
* BM25
    * use lucene BM25Similarities
    * the params k1 and b are trained with the qrels2014
```java
searcher.setSimilarity(new BM25Similarity(pa.k1, pa.b));
```
![](http://ipl.cs.aueb.gr/stougiannis/bm25_formula.png)

## Experiment Procedure
* preprocess
    + extract the downloaded compressed files to ../data/pmc-text
    + use the tool TransFormat to transform the files format
* index
    + read the trec format files
    + get the DOCID information, and get the text information
    + use Lucene index writer to index
* train
    + traverse each k1 and b and do search 2014 queries
    + compare result choose the best k1 and b
    + from the following code, it is obvious that the training set is topics2014.xml
```java
String queries14 = "../data/topics2014.xml";
String queries15 = "../data/topics2015A.xml";
String indexFolder = "../data/index";
String qrelsFile14 = "../data/qrels2014.txt";
String queryResult15A = "../data/queryResult15A.txt";
Param pa = trainBM25Param(queries14, indexFolder, qrelsFile14);
```
* search
    + search 2015A queries with the best k1 and b

