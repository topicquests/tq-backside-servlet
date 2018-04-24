#!/bin/sh
java -Xms1024M -Xmx4G -cp ./lib/*:/target/tq-backside-servlet-0.1.1-SNAPSHOT-jar-with-dependencies.jar org.topicquests.backside.servlet.Main
