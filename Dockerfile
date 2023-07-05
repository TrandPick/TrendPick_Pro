FROM openjdk:17-jdk-alpine
ADD https://github.com/pinpoint-apm/pinpoint/releases/download/v2.5.2/pinpoint-agent-2.5.2.tar.gz /usr/local/
RUN tar -zxvf /usr/local/pinpoint-agent-2.5.2.tar.gz -C /usr/local/
RUN sed -i 's/profiler\.transport\.grpc\.collector\.ip=127\.0\.0\.1/profiler.transport.grpc.collector.ip=101.101.219.220/' /usr/local/pinpoint-agent-2.5.2/profiles/release/pinpoint.config
RUN sed -i 's/profiler\.collector\.ip=127\.0\.0\.1/profiler.collector.ip=101.101.219.220/' /usr/local/pinpoint-agent-2.5.2/profiles/release/pinpoint.config
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", \
"-javaagent:/usr/local/pinpoint-agent-2.5.2/pinpoint-bootstrap-2.5.2.jar", \
"-Dpinpoint.agentId=${PINPOINT_AGENT_ID}", \
"-Dpinpoint.applicationName=${PINPOINT_APP_NAME}", \
"-Dpinpoint.config=/usr/local/pinpoint-agent-2.5.2/profiles/release/pinpoint.config", \
"-Dspring.profiles.active=prod", \
"/app.jar"]
