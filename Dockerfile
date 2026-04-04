FROM openjdk:21

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

CMD ["java", "-jar", "target/Finance-0.0.1-SNAPSHOT.jar"]