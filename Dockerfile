FROM eclipse-temurin:17-jre-focal

LABEL version="1.0"
LABEL maintainer="sarath vedha"
LABEL description="SpringBoot Mail App"

WORKDIR /app/jar
COPY target/SpringBootMail.jar /app/jar/SpringBootMail.jar

#Giving Write privilage for logs
RUN mkdir -m 777 /app/log

#Giving Write privilage for mail logs
RUN mkdir -m 777 /app/log/mail

#Random volume will be created for logs if not provided by user at runtime
#Random volume will be deleted after container is removed, so it is better to provide volume bind at runtime
VOLUME /app/log

EXPOSE 80
ENV APP_PROFILE=dev
ENV APP_PORT=80
ENV LOG_PATH=/app/log/mail
ENV MAIL_USERNAME=username
ENV MAIL_PASSWORD=password
ENV ENV_ARGS="Env Args"
HEALTHCHECK --interval=50s --timeout=3s CMD curl -fkv http://localhost/actuator/health || exit 1

#Giving Write privilage for heap and gc logs
RUN mkdir -m 777 /app/log/java

RUN useradd -r -d /app vedha
USER vedha

ENTRYPOINT [ "java", "-XX:MinRAMPercentage=25", "-XX:MaxRAMPercentage=50", "-XX:+HeapDumpOnOutOfMemoryError", "-XX:HeapDumpPath=/app/log/java/heapdump.log", "-Xlog:gc:/app/log/java/gc.log", "-DJVMArgs=JVM Args", "-jar", "SpringBootMail.jar", "--program.args=Program Args", "Command Args" ]

#ENTRYPOINT [ "java", "-jar", "SpringBootMail.jar" ]