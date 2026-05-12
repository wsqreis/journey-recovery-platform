# Architecture overview

The platform is organized as a small set of deployable services backed by shared domain modules.

## Services

### experience-api

Provides read-focused HTTP endpoints for disruption cases and recommendations.

### decision-worker

Consumes disruption events, updates case state, and emits recommendation updates.

## Shared libraries

### domain

Contains the core decisioning model and business rules.

### contracts

Contains DTOs and event contracts shared across services.

### testing-support

Contains shared test utilities and integration test helpers.

## Supporting infrastructure

- Kafka for event ingestion and publication
- PostgreSQL for durable case state
- Redis for caching and idempotency
