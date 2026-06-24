FROM docker.io/ibm-semeru-runtimes:open-21-jdk-noble AS builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM docker.io/ibm-semeru-runtimes:open-21-jre-noble
WORKDIR /app
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
COPY --chmod=755 src/main/docker/scripts/*.sh ./

RUN chgrp -R 0 /app && chmod -R g=u /app

ENV JAVA_MAIN_CLASS=org.springframework.boot.loader.launch.JarLauncher

# Setting those env will cause the server to run with https support
ENV SERVER_SSL_PEM_CRT=/etc/k5/security/servicecert/tls.crt

ENTRYPOINT ["/app/run-server.sh"]
