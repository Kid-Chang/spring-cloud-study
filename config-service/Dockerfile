FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY apiEncryptionKey.jks apiEncryptionKey.jks
COPY target/config-service-1.0.0.jar ConfigServer.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar", "ConfigServer.jar"]