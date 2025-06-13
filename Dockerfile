# Stage 1: Build Spring Boot app with Gradle and Java 21 Alpine
FROM gradle:8-jdk21-alpine AS build

WORKDIR /app

COPY build.gradle settings.gradle /app/
COPY src /app/src

RUN gradle clean build --no-daemon --warning-mode all

# Stage 2: Final image with Eclipse Temurin JRE Alpine + your app + H2 jar
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy built jar and H2 jar
COPY --from=build /app/build/libs/*.jar app.jar
COPY h2-2.3.232.jar ./h2.jar

# Copy entrypoint script
COPY entrypoint.sh ./entrypoint.sh
RUN chmod +x ./entrypoint.sh

# Set environment variable for Spring datasource inside container only
ENV SPRING_DATASOURCE_URL=jdbc:h2:file:/data/tracklore-db;AUTO_SERVER=TRUE

EXPOSE 8080 9092

ENTRYPOINT ["./entrypoint.sh"]