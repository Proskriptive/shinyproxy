#!/bin/sh
set -e
mvn clean package
sudo systemctl stop shinyproxy
sudo cp -v target/shinyproxy*.jar /opt/shinyproxy/shinyproxy.jar
if [ "$(sudo docker ps -q)" != "" ]; then
    sudo systemctl restart docker
fi
sudo systemctl start shinyproxy
