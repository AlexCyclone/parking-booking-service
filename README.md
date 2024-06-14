# Parking Spot Booking Service
This service handles parking spot reservations as part of the [Parking project](https://github.com/AlexCyclone/parking-compose-demo).

### Guides

This application uses **profiling**:
- `local` - to run the application locally
- `secret` - provides the credentials required to run the application (use [`application-secret.yaml.example`](https://github.com/AlexCyclone/parking-booking-service/blob/main/application-secret.yaml.example))
- `rest-mode` - Synchronous REST API integration
- `kafka-mode` - activates Kafka messaging integration

Swagger ui - `http://{app-host}/swagger-ui/index.html`

OpenApi Specification - `http://{app-host}/api-docs`

### Docker Compose support
This project contains a Docker Compose file named `compose.yaml`.
In this file, the following services have been defined:

* postgres: [`postgres:16.3-alpine`](https://hub.docker.com/_/postgres)
* zookeeper: [`confluentinc/cp-zookeeper:7.6.1`](https://hub.docker.com/r/confluentinc/cp-zookeeper)
* kafka: [`confluentinc/cp-kafka:7.6.1`](https://hub.docker.com/r/confluentinc/cp-kafka)

### Environment Variables
- `ENV_POSTGRES_HOST` - Postgres hostname
- `ENV_EUREKA_HOST` - Eureka server hostname
- `ENV_KAFKA_BOOTSTRAP_HOST` - Kafka host

#### Secrets:
- `ENV_POSTGRES_USERNAME` - Postgres username
- `ENV_POSTGRES_PASSWORD` - Postgres bucket
- `ENV_JWT_SECRET`: JWT secret longer than 256 bit
