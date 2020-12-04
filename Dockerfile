FROM gradle:latest AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar

FROM openjdk:latest

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/Authentication-1.0-all.jar /app/Authentication.jar
ENTRYPOINT ["java", "-jar", "/app/Authentication.jar"]
