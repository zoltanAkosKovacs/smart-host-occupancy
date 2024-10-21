# Use the Eclipse Temurin JDK base image
FROM eclipse-temurin:21-jdk-jammy

# Set the working directory in the container
WORKDIR /occupancy

# Copy the project files
COPY . .

# Expose port 8080
EXPOSE 8080

# Start the application using the run.sh script
CMD ["./run.sh"]