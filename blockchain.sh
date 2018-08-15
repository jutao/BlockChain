#!/bin/bash

set -e

# Check if the jar has been built.
if [ ! -e target/blockchain-java-jar-with-dependencies.jar ]; then
  echo "Compiling blockchain project to a JAR"
  mvn package -DskipTests
fi

java -jar target/blockChain-java-jar-with-dependencies.jar "$@"