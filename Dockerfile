# FROM maven AS build_stage
# WORKDIR /usr/src/app
# COPY pom.xml .
# COPY src/ src/
# COPY .m2/ .m2/
# RUN mvn clean install

FROM openjdk:19
ARG model_service_url=http://localhost:8080/predict
ENV model_service_url=$model_service_url
# COPY --from=build_stage /usr/src/app/target/app-0.0.1-SNAPSHOT.jar app.jar

RUN echo $([PWD]: pwd) && echo $(tree -al)

COPY ./target/app-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8000
CMD ["java", "-jar", "app.jar"]
