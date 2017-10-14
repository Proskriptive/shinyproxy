FROM maven

RUN apt-get update
RUN apt-get install sudo socat

COPY . /usr/src/shinyproxy
WORKDIR /usr/src/shinyproxy
RUN mvn package
RUN mkdir /opt/shinyproxy
RUN cp target/*.jar /opt/shinyproxy

COPY example/application.yml /opt/shinyproxy/application.default.yml

COPY start-shiny.sh /opt/shinyproxy/start-shiny.sh
RUN chmod +x /opt/shinyproxy/start-shiny.sh

WORKDIR /opt/shinyproxy
CMD ["/opt/shinyproxy/start-shiny.sh"]
