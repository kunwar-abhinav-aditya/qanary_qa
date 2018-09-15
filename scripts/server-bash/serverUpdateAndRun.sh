#!/bin/bash
pkill -f jar
sleep 5
Component1=$1
Component2=$2

if [ "$Component2" == "" ];then
    echo "-------------------------------------------------No Commponent2 supplied------------------------------------"
else
    echo "-------------------------------------------------Commponent2 supplied------------------------------------"
fi

STARDOG=/qanarySetup/Applications/stardog/stardog-4.1.3
QANARY=/qanarySetup/Applications/workspace/qanary_qa
QANARY_LOG=/qanarySetup/Applications/workspace/qanary_logs
ApplicationPrefix='spring.application.name='

#cat $QANARY/$Component1/src/main/resources/config/application.properties
#cat $QANARY/$Component2/src/main/resources/config/application.properties

App1=$(grep -i 'spring.application.name.*' $QANARY/$Component1/src/main/resources/config/application.properties)
echo $App1
ApplicationName1="${App1/$ApplicationPrefix/}"

App2=$(grep -i 'spring.application.name.*' $QANARY/$Component2/src/main/resources/config/application.properties)
echo $App2
ApplicationName2="${App2/$ApplicationPrefix/}"




# echo "-------------------------------------------------Git Updates-----------------------------------------------------------"
cd $QANARY
git checkout arun
git reset --hard
git pull
#mvn clean install -DskipDockerBuild
echo $STARDOG
echo $QANARY
echo $QANARY_LOG
echo $Component1
echo $Component2
echo $ApplicationName1
echo $ApplicationName2
echo "-------------------------------------------------Stardog-----------------------------------------------------------"
rm /qanarySetup/Applications/stardog/stardog-4.1.3/system.lock
$STARDOG/bin/stardog-admin server stop
sleep 2
$STARDOG/bin/stardog-admin server start
sleep 10
cd $QANARY/qanary_pipeline-template
mvn clean install -DskipDockerBuild
cd $QANARY/$Component1
mvn clean install -DskipDockerBuild
if [ "$Component2" == "" ];then
    echo "-------------------------------------------------No Commponent2 supplied------------------------------------"
else
  cd $QANARY/$Component2
  mvn clean install -DskipDockerBuild
fi

echo "-------------------------------------------------Starting Pipeline and Component------------------------------------"
cd $QANARY
nohup java -jar qanary_pipeline-template/target/qa.pipeline-1.1.0.jar &
sleep 10
nohup java -jar $Component1/target/*.jar 2>$QANARY_LOG/$ApplicationName1"_error".log 1>$QANARY_LOG/$ApplicationName1"_out".log &
sleep 10
if [ "$Component2" == "" ]; then
    echo "-------------------------------------------------No Commponent2 supplied------------------------------------"
else
    nohup java -jar $Component2/target/*.jar 2>$QANARY_LOG/$ApplicationName2"_error".log 1>$QANARY_LOG/$ApplicationName2"_out".log &
fi

echo "-------------------------------------------------Starting Lcevaluator------------------------------------------------"
sleep 10
if [ "$Component2" == "" ]; then
  echo "0,\"$ApplicationName1\"" > /qanarySetup/Applications/workspace/qanary_qa/lcevaluator/src/main/resources/pipelines.csv
else
  echo "0,\"$ApplicationName1,$ApplicationName2\"" > /qanarySetup/Applications/workspace/qanary_qa/lcevaluator/src/main/resources/pipelines.csv
fi

sleep 2
cd /qanarySetup/Applications/workspace/qanary_qa/lcevaluator
nohup mvn clean install -DskipDockerBuild >$QANARY_LOG/$ApplicationName1"_$ApplicationName2""_lcevaluator".log 2>&1 &
