FROM maven:3.9.9-amazoncorretto-21 AS build

WORKDIR /app

# Copy Maven mirror config
COPY settings.xml /root/.m2/settings.xml

COPY pom.xml .
COPY src ./src

RUN mvn -s /root/.m2/settings.xml clean package -DskipTests


FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
EXPOSE 8082

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
