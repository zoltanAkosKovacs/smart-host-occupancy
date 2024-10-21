#!/bin/bash

# Build the application using Maven
./mvnw clean install

# Start the application
java -jar target/*.jar