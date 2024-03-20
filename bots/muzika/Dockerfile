FROM openjdk:21-jdk-slim
ARG VERSION
ARG BOT_OWNER
ARG MUZIKA_KEY
ARG LEGACY_KEY
ENV BOT_OWNER=$BOT_OWNER
ENV MUZIKA_KEY=$MUZIKA_KEY
ENV VERSION=$VERSION
ENV LEGACY_KEY=$LEGACY_KEY

COPY bots/muzika/config.txt config.txt
RUN sed -i 's/BOT_TOKEN_HERE/${MUZIKA_KEY}/g' config.txt
RUN sed -i 's/BOT_OWNER/${BOT_OWNER}/g' config.txt
RUN sed -i 's/LEGACY_KEY/${LEGACY_KEY}/g' config.txt
RUN apt-get update -y \
    && apt-get install curl -y \
    && rm -rf /var/lib/apt/lists/* /var/cache/apt/archives/*
RUN curl -L https://github.com/jagrosh/MusicBot/releases/download/${VERSION}/JMusicBot-${VERSION}.jar -o JMusicBot-${VERSION}.jar
ENTRYPOINT java -Dnogui=true -jar JMusicBot-${VERSION}.jar