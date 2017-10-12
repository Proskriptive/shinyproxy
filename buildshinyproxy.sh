#!/bin/sh
set -e
mvn clean package
sudo cp -v target/shinyproxy*.jar /opt/shinyproxy/shinyproxy.jar
sudo systemctl stop shinyproxy
sudo systemctl restart docker
sudo systemctl start shinyproxy
