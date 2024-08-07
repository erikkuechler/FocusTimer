#
# Build stage
#
FROM gradle:jdk17-jammy AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build --no-daemon

LABEL org.name="ErikNic"
#
# Package stage
#
FROM eclipse-temurin:17-jdk-jammy
COPY --from=build /home/gradle/src/build/libs/focus-timer-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]