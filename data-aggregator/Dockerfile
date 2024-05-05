# Runtime
FROM amazoncorretto:21
ENV JAVA_OPTS="--add-opens java.base/sun.nio.ch=ALL-UNNAMED"
EXPOSE 8080
ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]
