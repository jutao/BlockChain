#!/bin/bash

set -e

# Check if the jar has been built.
if [ ! -e target/blockChain-jar-with-dependencies.jar ]; then
  echo "Compiling blockChain project to a JAR"
  mvn package -DskipTests
fi

java -jar target/blockChain-jar-with-dependencies.jar "$@"



#在项目跟目录运行

#帮助
#./blockchain.sh -h

#添加区块
#./blockchain.sh -add "比特币的内容"

#打印区块链
#./blockchain.sh -print

