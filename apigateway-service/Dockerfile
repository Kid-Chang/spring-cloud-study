FROM openjdk:21-ea-1-jdk-slim
VOLUME /tmp
COPY target/apigateway-service-1.0.0.jar ApiGatewayServer.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar", "ApiGatewayServer.jar"]