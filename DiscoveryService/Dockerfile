FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/DiscoveryService-0.0.1-SNAPSHOT.jar DiscoveryServer.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar", "DiscoveryServer.jar"]