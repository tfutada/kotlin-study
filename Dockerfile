# Base image with OpenJDK 17
FROM openjdk:17

# Expose port 5106 for the application
EXPOSE 5106

# Create app and logs directories
RUN mkdir /app /app/logs

# Copy the JAR file from the current directory to the /app directory in the container
COPY kotlin-study-all.jar /app/kotlin-study-all.jar

# Set the working directory
WORKDIR /app

# Command to run the application
ENTRYPOINT ["java", "-jar", "kotlin-study-all.jar"]
