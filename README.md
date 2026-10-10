# Quarkus Crud Demo

## Software and requirements

**Software/tools:** Quarkus, Java, ORM Panache, MapStruct, Flyway, OpenAPI for Quarkus, Validations, PostgreSQL, Password4j, Maven

**Requirements:** Java, PostgreSQL/Docker

## Endpoints

- Base path:
http://localhost:5000

- Swagger docs:
http://localhost:5000/openapi

- Swagger UI base path:
http://localhost:5000/q/swagger-ui/

- Quarkus dev page:
http://localhost:5000/q/dev-ui

- Database viewer:
http://localhost:5000/q/dev-ui/quarkus-agroal/database-view

## Postman

- Postman collection: [Collection](postman/quarkus-crud.postman_collection.json)
- Postman environment: [Environment](postman/quarkus-crud.postman_environment.json)

## Setup the project

1. **Setup datasource via Docker:**

    ```bash
    cd src/main/docker/

    docker compose -p quarkus-datasource up -d
    ```

2. **Set property for the first application start `quarkus.flyway.migrate-at-start=true` after that turn it to** `false`

**Add arguments for profile while running the app (NOT required):** `-Dquarkus.profile=development`

## Run the project with dev environment

```bash
./mvnw quarkus:dev
```

## Build production jar

```bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

**Run the jar via following script:**

```bash
cd target/

java -jar <your_application_jar>
```


## Build native image (Optional)

> [!IMPORTANT]
> This step requires running Docker.

```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

The native executable will be stored in `target` folder. The target OS for the native image is `Linux`.

### Default users

1. **Customer user:**

    Username: `abv@abv.bg`

    Password: `Pass123!`

---

2. **Admin user:**

    Username: `admin@abv.bg`

    Password: `Pass123!`

## Contact me

[![Static Badge](https://img.shields.io/badge/Github-%2366099c?style=for-the-badge&logo=github&logoColor=black&labelColor=white)](https://github.com/balantaim)
[![Static Badge](https://img.shields.io/badge/google_play-%23057308?style=for-the-badge)](https://play.google.com/store/apps/dev?id=4991626043223074729)
[![Static Badge](https://img.shields.io/badge/Linkedin-%23321ee6?style=for-the-badge&logoColor=black&labelColor=white)](https://www.linkedin.com/in/martin-atanasov-47550b1a2/)