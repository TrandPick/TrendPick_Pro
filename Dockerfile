FROM openjdk:17-jdk-alpine
ADD https://github.com/pinpoint-apm/pinpoint/releases/download/1.7.3/pinpoint-agent-1.7.3.tar.gz /usr/local
RUN tar -zxvf /usr/local/pinpoint-agent-1.7.3.tar.gz -C /usr/local
RUN sed -i 's/profiler.collector.ip=127.0.0.1/profiler.collector.ip=101.101.219.220/g' /usr/local/pinpoint.config
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", \
"-javaagent:/usr/local/pinpoint-bootstrap-1.7.3.jar", \
"-Dpinpoint.applicationName=TrendPick", \
"-Dpinpoint.config=/usr/local/pinpoint.config", \
"-Dspring.profiles.active=prod", \
"/app.jar"]
