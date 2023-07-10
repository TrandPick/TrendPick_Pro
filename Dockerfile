# Define a build stage
FROM openjdk:17-jdk-alpine AS build
WORKDIR /workspace/app

# Copy your source code
COPY . .

# Build the application
RUN ./gradlew clean build

# Start a new stage
FROM openjdk:17-jdk-alpine

# Add Pinpoint
ADD https://github.com/pinpoint-apm/pinpoint/releases/download/v2.5.2/pinpoint-agent-2.5.2.tar.gz /usr/local
RUN tar -zxvf /usr/local/pinpoint-agent-2.5.2.tar.gz -C /usr/local

# Update the Pinpoint configuration
RUN sed -i 's/profiler.transport.grpc.collector.ip=127.0.0.1/profiler.transport.grpc.collector.ip=10.41.183.156/g' /usr/local/pinpoint-agent-2.5.2/pinpoint-root.config
RUN sed -i 's/profiler.collector.ip=127.0.0.1/profiler.collector.ip=10.41.183.156/g' /usr/local/pinpoint-agent-2.5.2/pinpoint-root.config

# Copy the built JAR from the build stage
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# Expose the required port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar", \
"-javaagent:/usr/local/pinpoint-agent-2.5.2/pinpoint-bootstrap-2.5.2.jar", \
"-Dpinpoint.applicationName=TrendPick", \
"-Dpinpoint.config=/usr/local/pinpoint-agent-2.5.2/pinpoint-root.config", \
"-Dspring.profiles.active=prod", \
"/app.jar"]
