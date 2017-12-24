#!/bin/sh
# Define some constants
JAR=lib
BIN=bin
SRC=src
classpath=$JAR/dom4j-2.1.0.jar:$JAR/commons-math3-3.3.jar:$JAR/jsoup-1.7.3.jar:$JAR/lucene-analyzers-common-5.0.0.jar:/$JAR/lucene-codecs-5.0.0.jar:$JAR/lucene-core-5.0.0.jar:$JAR/lucene-queries-5.0.0.jar:$JAR/lucene-queryparser-5.0.0.jar
# First remove the sources.list file if it exists and then create the sources file of the project
rm -f $SRC/sources
find $SRC -name *.java > $SRC/sources.list
# First remove the ONSServer directory if it exists and then create the bin directory of ONSServer
rm -rf $BIN
mkdir $BIN
# Compile the project

javac -d $BIN -classpath $classpath @$SRC/sources.list

java -cp ${BIN}:$classpath TransOfFormat.Test

java -cp ${BIN}:$classpath indexing.Indexer

java -cp ${BIN}:$classpath trec.SearchTopics
