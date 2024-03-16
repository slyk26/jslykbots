FROM maven:3.9.6-amazoncorretto-21-debian-bookworm AS build
ENV HOME=/usr/app
WORKDIR $HOME
ADD ../../pom.xml $HOME/pom.xml
RUN  sed -i '/<modules>/,/<\/modules>/c\<modules>\n<module>shared</module>\n<module>bots/muzika</module>\n</modules>' $HOME/pom.xml
ADD ../../shared/pom.xml $HOME/shared/pom.xml
ADD ../../bots/muzika/pom.xml $HOME/bots/muzika/pom.xml

RUN mvn -pl shared verify
ADD shared $HOME/shared
RUN mvn -pl shared install
RUN mvn -pl bots/muzika verify
ADD bots/muzika $HOME/bots/muzika
RUN mvn -pl shared,bots/muzika package

FROM openjdk:21-jdk-slim
COPY --from=build /usr/app/bots/muzika/target/muzika-1.0-SNAPSHOT-jar-with-dependencies.jar /app/muzika.jar
RUN apt-get update -y \
    && apt-get install curl python3 -y \
    && rm -rf /var/lib/apt/lists/* /var/cache/apt/archives/*
RUN curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -o yt-dlp_linux && chmod +x yt-dlp_linux
ENTRYPOINT java -jar /app/muzika.jar