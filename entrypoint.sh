#!/bin/sh
# Start H2 TCP server in background
java -cp h2.jar org.h2.tools.Server -tcp -tcpAllowOthers -tcpPort 9092 &

# Start Spring Boot application
java -jar app.jar