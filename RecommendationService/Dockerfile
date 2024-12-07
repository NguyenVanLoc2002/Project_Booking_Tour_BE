# Stage 1: Build ứng dụng
FROM gradle:8.11-jdk17-alpine AS build

# Đặt thư mục làm việc cho stage build
WORKDIR /app

# Copy toàn bộ project vào thư mục /app trong container
COPY --chown=gradle:gradle . .

# Chạy Gradle clean và build để tạo file JAR
RUN gradle clean build --no-daemon

# Stage 2: Tạo image chạy ứng dụng
FROM eclipse-temurin:17-jdk-alpine

# Đặt thư mục làm việc cho container cuối cùng
WORKDIR /app

# Copy file JAR từ stage build vào container
COPY --from=build /app/build/libs/RecommendationService-0.0.1-SNAPSHOT.jar RecommendationService-0.0.1-SNAPSHOT.jar

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "/app/RecommendationService-0.0.1-SNAPSHOT.jar"]
