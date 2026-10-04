# Quarkus Crud Project

Swagger base path:
http://localhost:5000/q/swagger-ui/

Quarkus dev page:
http://localhost:5000/q/dev-ui

Database viewer:
http://localhost:5000/q/dev-ui/quarkus-agroal/database-view

Setup datasource via Docker:

```bash
cd src/main/docker/

docker compose -p quarkus-datasource up -d
```

Add arguments for profile: `-Dquarkus.profile=prod`