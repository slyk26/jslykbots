# Discord Bots in java

trying out a monorepo architecture to create discontinued Discord Bots

### Planned

- [x] Markov bot -> **markov module**
- [x] Generic Settings for Bots to handle Bot specific settings (db, etc.)
- [x] Music bot (similar to Rhytmn Bot) -> **muzika module**
  - [x] works with Soundcloud (scuffed), YT Videos and Livestreams
  - [x] Soundcloud has query search, YT has link and query search
  - [ ] add user awareness (leave if no listeners, etc ..)
  - [ ] make pretty
  - [ ] Docker Image
- [ ] help command for all bots
- [ ] Utility Bot (Polls, Confessions and whatnot)
- [ ] AI Chatting somewhere
- cont.


___

### setup db

run `setup.sh` [found here](./db_setup) to boot up the db and create the tables in it automatically

### setup muzika

* get a yt-dlp binary and replace the file in the muzika module

### use in production

- clone repo
- create .env in root directory with .env file example below
- setup.sh
- watch logs with `docker logs markov --follow`

### .env file - so I don't get dementia
```
# discord keys
MARKOV_KEY=
MUZIKA_KEY=

#postgres variables
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_HOST=db
POSTGRES_PORT=
POSTGRES_URL=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_USER}

```