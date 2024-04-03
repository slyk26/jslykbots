FROM maven:3.9.6-amazoncorretto-21-debian-bookworm AS build
ENV HOME=/usr/app
WORKDIR $HOME

ADD bajbot/pom.xml $HOME/pom.xml
RUN sed -i '/<modules>/,/<\/modules>/c\<modules>\n<module>shared</module>\n<module>bots/bajbot</module>\n</modules>' $HOME/pom.xml
ADD shared/pom.xml $HOME/shared/pom.xml
ADD bots/bajbot/pom.xml $HOME/bots/bajbot/pom.xml

RUN mvn -pl shared verify
ADD shared $HOME/shared
RUN mvn -pl shared install
RUN mvn -pl bots/bajbot verify
ADD bots/bajbot $HOME/bots/bajbot
RUN mvn -pl shared,bots/bajbot package

FROM openjdk:21-jdk-slim
COPY --from=build /usr/app/bots/bajbot/target/bajbot-1.0-SNAPSHOT-jar-with-dependencies.jar /app/bajbot.jar
ENTRYPOINT java -jar /app/bajbot.jar