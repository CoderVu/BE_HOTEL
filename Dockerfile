# Build stage
FROM maven:3.8.2-openjdk-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Package stage
FROM openjdk:17-jdk-slim
COPY --from=build /app/target/Booking_Hotel-0.0.1-SNAPSHOT.jar demo.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","demo.jar"]
