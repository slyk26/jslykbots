# Discord Bots in java

trying out a monorepo architecture to create discontinued Discord Bots

### Planned

- [ ] Markov bot
- [ ] Music bot (similar to Rhytmn Bot)
- [ ] Utility Bot (Polls, Confessions and whatnot)
- [ ] AI Integration somewhere
- cont.

### setup db

run `setup.sh` [found here](./db_setup) to boot up the db and create the tables in it automatically

### .env file - so I don't get dementia

```
# discord keys
MARKOV_KEY=

#postgres variables
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_HOST=
POSTGRES_PORT=
POSTGRES_URL=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_USER}

```