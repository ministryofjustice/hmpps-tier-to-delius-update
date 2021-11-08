# hmpps-tier-to-delius-update

Consumes tier calculation complete events from the hmpps-domain-events topic, retrieves the tier from hmpps-tier and writes the tier to delius via community-api

## Running locally against docker

```shell
docker compose up -d 
```

Run this application with the following configuration:

SPRING_PROFILES_ACTIVE=dev;SERVER_PORT=8099

## testing

Requires localstack to be running, so

```shell
docker compose up -d 
./gradlew check
```