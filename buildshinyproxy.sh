#!/bin/sh
set -e
mvn clean package
sudo cp -v target/shinyproxy*.jar /opt/shinyproxy/shinyproxy.jar
sudo systemctl restart shinyproxy
