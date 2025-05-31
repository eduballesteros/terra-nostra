# api/Dockerfile

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/terra-nostra-0.0.1-SNAPSHOT.jar"]
