git pull
mvn clean install
sudo -i cp -v  ~/.m2/repository/eu/openanalytics/shinyproxy/0.9.2/*.jar /opt/shinyproxy
sudo systemctl restart shinyproxy
