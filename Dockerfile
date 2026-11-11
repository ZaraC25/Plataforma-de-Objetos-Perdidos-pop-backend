FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S pop && adduser -S pop -G pop
USER pop
COPY --from=build /app/target/*.jar app.jar
HEALTHCHECK --interval=30s --timeout=3s RETRIES=3 CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
