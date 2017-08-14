#!/bin/sh
set -e
mvn clean install
cd ~/.m2/repository/eu/openanalytics/shinyproxy
# TODO find a more stable solution
sudo cp -v "$(find . -mindepth 1 -maxdepth 1 -type d -printf '%f\n' | sort -V | tail -n 1)"/*.jar /opt/shinyproxy
sudo systemctl restart shinyproxy
