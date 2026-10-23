FROM eclipse-temurin:17-jre-alpine
EXPOSE 8082
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

# DEVELOP VERSION - preserved as comments
# FROM eclipse-temurin:17-jre
# WORKDIR /app
# COPY target/*.jar app.jar
# EXPOSE 8082
# ENTRYPOINT ["java", "-jar", "app.jar"]
