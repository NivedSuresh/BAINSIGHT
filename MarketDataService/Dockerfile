FROM openjdk:21
ARG JAR_FILE=target/*.jar
COPY target/LiquidityService-0.0.1-SNAPSHOT.jar liquidity-service.jar
ENTRYPOINT ["java", "-jar", "/liquidity-service.jar"]