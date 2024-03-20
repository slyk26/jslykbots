# Discord Bots in java

trying out a monorepo architecture to create discontinued Discord Bots

### Planned

- [x] Markov bot -> **markov module**
- [x] Generic Settings for Bots to handle Bot specific settings (db, etc.)
- [x] Music bot (similar to Rhytmn Bot) -> https://github.com/jagrosh/MusicBot in a Dockerfile
- [x] help command for all bots
- [ ] Bajbot (Polls, Confessions and whatnot)
- [x] AI Chatting somewhere
- cont.

___


### new setup

- clone repo
- create .env in root directory with .env file example below
- setup.sh
- watch logs with `docker logs markov --follow`


#### setup db

run `setup.sh` [found here](./db_setup) to boot up the db and create the tables in it automatically

___

### .env file - so I don't get dementia
```
# discord keys
MARKOV_KEY=
MUZIKA_KEY=

# musicbot (credits to https://github.com/jagrosh/MusicBot)
BOT_OWNER=
VERSION=

# character for legacy commands
LEGACY_KEY=

# ai stuff
OPENAI_KEY=
OPENAI_PRE_PROMPT=

#postgres variables
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_HOST=db
POSTGRES_PORT=
POSTGRES_URL=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_USER}

```