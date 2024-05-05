#
##Runtime
#FROM amazoncorretto:21
#
#ADD target/*.jar app.jar
#WORKDIR /app
#COPY --from=build /build/target/app-name-*.jar /app/
#
#EXPOSE 8080
#CMD java -jar app-name-*.jar
#
#

#Runtime
FROM amazoncorretto:21

EXPOSE 8080
ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]