FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY build/libs/libraff-store-0.0.1-SNAPSHOT.jar libraff-store.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "libraff-store.jar"]

