#!/bin/bash
socat TCP-LISTEN:2375,reuseaddr,fork UNIX-CLIENT:/var/run/docker.sock &
sleep 1

if [ -f conf/application.yml ]; then
    cp conf/application.yml .
else
    cp application.default.yml application.yml
fi

sed "s/@@DOCKER_HOST@@/$(/sbin/ip route|awk '/default/ { print $3 }')/g" -i /opt/shinyproxy/application.yml
java -jar /opt/shinyproxy/*.jar
