FROM maven AS build_stage
WORKDIR /usr/src/app/
COPY pom.xml .
COPY src/ src/
RUN mvn clean install -DskipTests

FROM openjdk:19 AS run_stage
ARG model_service_url=http://localhost:8080/predict
ENV model_service_url=$model_service_url
WORKDIR /home/
COPY --from=build_stage /usr/src/app/target/app-*.jar app.jar
EXPOSE 8000
CMD ["java", "-jar", "app.jar"]
