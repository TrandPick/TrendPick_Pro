# Stage 1: Pinpoint setup
# FROM alpine:3.14 as pinpoint-setup
# WORKDIR /usr/local
# ADD https://github.com/pinpoint-apm/pinpoint/releases/download/v2.5.2/pinpoint-agent-2.5.2.tar.gz /usr/local
# RUN tar -zxvf pinpoint-agent-2.5.2.tar.gz && \
#     sed -i 's/profiler.transport.grpc.collector.ip=127.0.0.1/profiler.transport.grpc.collector.ip=10.41.183.156/g' pinpoint-agent-2.5.2/pinpoint-root.config && \
#     sed -i 's/profiler.collector.ip=127.0.0.1/profiler.collector.ip=10.41.183.156/g' pinpoint-agent-2.5.2/pinpoint-root.config

# Stage 2: Build final image
FROM openjdk:17-jdk-alpine
WORKDIR /app
# COPY --from=pinpoint-setup /usr/local/pinpoint-agent-2.5.2/ ./pinpoint-agent
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", \
# "-javaagent:/app/pinpoint-agent/pinpoint-bootstrap-2.5.2.jar", \
# "-Dpinpoint.applicationName=TrendPick", \
# "-Dpinpoint.config=/app/pinpoint-agent/pinpoint-root.config", \
"-Dspring.profiles.active=prod", \
"/app/app.jar"]
