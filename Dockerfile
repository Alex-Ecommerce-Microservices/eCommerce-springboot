
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml và tải dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code và build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy file jar (ecommerce-0.0.1-SNAPSHOT.jar)
COPY --from=build /app/target/ecommerce-0.0.1-SNAPSHOT.jar app.jar

# Monolith chạy cổng 8081
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]