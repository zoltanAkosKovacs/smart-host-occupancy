#!/bin/sh

# Build the application using Maven
./gradlew build

# Start the application
java -jar build/libs/occupancy-0.0.3-SNAPSHOT.jar