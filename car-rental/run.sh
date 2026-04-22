#!/bin/bash
echo "Compiling..."
find src -name "*.java" > sources.txt
mkdir -p out
javac -sourcepath src -d out @sources.txt && echo "Done. Starting..." && java -cp out com.carrental.Main
