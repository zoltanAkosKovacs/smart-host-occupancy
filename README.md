# Getting Started

# How to run locally

1. Navigate to project root folder and open cmd
2. run "./run.sh"
3. Application is up and running on localhost:8080

OR

1. Navigate to project root folder and open cmd
2. Run "./mvnw clean install" (optionally with -DskipTests)
3. Run "java -jar ./target/occupancy-0.0.1-SNAPSHOT.jar"
4. Application is up and running on localhost:8080

# How to run docker

1. Navigate to project root folder and open cmd
    1.1 Currently there's an issue with maven wrapper within docker,
        so you might have to run ".\mvnw clean install" so the jar is copied to docker
2. Run "docker build -t occupancy ."
3. Run "docker run -p 8080:8080 occupancy"
4. Application is up and running on localhost:8080

# How to test

1. When running, the application can be called on localhost:8080/occupancy endpoint
2. This can be done via
    2.1 CURL
    2.2 Swagger UI - exposed on http://localhost:8080/swagger-ui/index.html
    2.3 Postman - collection can be found in the resources folder
3. Unit tests are automatically run when building