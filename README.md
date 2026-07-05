# payment-event-processor

Processes payment events through an event-driven pipeline.

I built this to understand how financial systems handle eventual consistency in practice — Kafka's theory is straightforward, but wiring producer, consumer, DLQ, and persistence with proper failure handling took a few days to get right. Most of the time went into deciding where each responsibility lives so infrastructure details don't leak into the domain.

## How it works

REST → producer publishes to Kafka → consumer reads → persists to PostgreSQL. A POST creates the payment as `PENDING`, saves to the database, and publishes an event. The consumer reads that event and moves the payment to `PROCESSING`.

The interesting part is failure handling on the consumer side: messages that can't be read or processed go to a dead letter queue instead of blocking the main topic.

## Stack

Kotlin 1.9 · Spring Boot 3.2 · Apache Kafka · PostgreSQL 16 · Flyway · Docker Compose

## Running locally

Requires Docker.

```bash
docker compose -f docker/docker-compose.yml up -d
# wait ~30 seconds for services to start

# create a payment
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{"amount": 150.00, "currency": "BRL", "senderId": "user-1", "receiverId": "user-2"}'

# check status
curl http://localhost:8080/api/v1/payments/{id}
```

API docs at `http://localhost:8080/swagger-ui.html`.

## Architecture

Hexagonal (ports and adapters): the domain has no Spring, Kafka, or JPA dependencies. Domain models carry zero framework annotations, and services depend only on interfaces. Any adapter can be swapped without touching business logic.
