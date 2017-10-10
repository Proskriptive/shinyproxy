#!/bin/sh
set -e
mvn clean package
sudo cp -v target/*.jar /opt/shinyproxy
sudo systemctl restart shinyproxy
