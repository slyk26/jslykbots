# Discord Bots in java

trying out a monorepo architecture to create discontinued Discord Bots

### Planned

- [x] Markov bot
- [ ] Generic Settings for Bots to handle Bot specific settings (db, etc.)
- [ ] Music bot (similar to Rhytmn Bot)
- [ ] Utility Bot (Polls, Confessions and whatnot)
- [ ] AI Integration somewhere
- cont.

### setup db

run `setup.sh` [found here](./db_setup) to boot up the db and create the tables in it automatically

### use in production

- clone repo
- create .env in root directory with .env file example below
- setup.sh
- watch logs with `docker logs markov --follow`

### .env file - so I don't get dementia
```
# discord keys
MARKOV_KEY=

#postgres variables
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_HOST=db
POSTGRES_PORT=
POSTGRES_URL=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_USER}

```