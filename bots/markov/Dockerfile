FROM maven:3.9.6-amazoncorretto-21-debian-bookworm AS build
ENV HOME=/usr/app
WORKDIR $HOME

ADD ../../pom.xml $HOME
ADD ../../shared/pom.xml $HOME/shared/pom.xml
ADD ../../bots/pom.xml $HOME/bots/pom.xml
ADD ../../bots/markov/pom.xml $HOME/bots/markov/pom.xml

RUN mvn -pl shared verify --fail-never
ADD shared $HOME/shared
RUN mvn -pl shared install
RUN mvn -pl bots/markov verify --fail-never
ADD bots/markov $HOME/bots/markov
RUN mvn -pl shared,bots/markov package

FROM openjdk:21-jdk-slim
COPY --from=build /usr/app/bots/markov/target/markov-1.0-SNAPSHOT-jar-with-dependencies.jar /app/markov.jar
ENTRYPOINT java -jar /app/markov.jar