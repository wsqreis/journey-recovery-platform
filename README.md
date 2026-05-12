# Journey Recovery Platform

A Java 17 platform for handling travel disruption cases from event ingestion through recommendation delivery and customer message drafting.

## Overview

The project models a travel recovery workflow around one core path:

1. a disruption event is published
2. the worker consumes it and updates case state
3. recommendation logic computes the next best action
4. the API exposes the case, recommendation, and draft customer message

The current implementation focuses on backend orchestration, decisioning, and operational readiness rather than a standalone frontend application.

## Repository layout

- `apps/experience-api` — Spring Boot API for case reads, recommendation reads, and draft message preview
- `apps/decision-worker` — Spring Boot worker for Kafka ingestion, persistence, and recommendation publication
- `libs/domain` — domain model and decisioning logic
- `libs/contracts` — shared contracts for events and API payloads
- `libs/testing-support` — shared test helpers
- `infra/docker` — local dependency stack
- `infra/k8s` — baseline deployment manifests
- `docs` — lightweight architecture notes

## Architecture

### Services

#### experience-api

Responsibilities:
- read disruption cases
- expose current recommendations
- preview customer-facing draft messages
- serve OpenAPI UI

Default local port:
- `8080`

#### decision-worker

Responsibilities:
- consume disruption events from Kafka
- compute next best actions
- persist case and segment state
- publish recommendation events
- enforce duplicate-event protection

Default local port:
- `8081`

### Shared dependencies

- PostgreSQL for durable case state
- Redis for caching and idempotency
- Kafka for disruption ingestion and downstream event publication

### Shared libraries

#### libs/domain

Contains:
- disruption case model
- customer context model
- recommendation scoring
- priority and SLA calculation

#### libs/contracts

Contains:
- disruption event payloads
- recommendation payloads
- API response contracts

## AI-assisted drafting

The API supports constrained message drafting behind an adapter interface.

### Behavior

- the Anthropic-backed drafting path is optional
- it is controlled by `anthropic.enabled`
- it uses a constrained instruction prefix and prompt caching on the stable system block
- if the model call fails, the API falls back to deterministic local drafting
- recommendation computation does not depend on the AI path

### Relevant implementation

- `apps/experience-api/src/main/java/com/travelcx/recovery/api/AnthropicDraftMessageService.java`
- `apps/experience-api/src/main/java/com/travelcx/recovery/api/FallbackDraftMessageService.java`
- `apps/experience-api/src/main/java/com/travelcx/recovery/api/DraftMessageService.java`

## Prerequisites

- Java 17+
- Gradle 9+
- Docker-compatible container runtime

## Local development

### 1. Start infrastructure

```bash
docker compose -f infra/docker/docker-compose.yml up -d
```

This starts:
- PostgreSQL
- Redis
- Kafka

### 2. Start the API

```bash
gradle bootRun --project-dir apps/experience-api --args='--server.port=8080'
```

### 3. Start the worker

```bash
gradle bootRun --project-dir apps/decision-worker
```

### 4. Run the test suite

```bash
gradle clean test
```

## Configuration

### experience-api

Key local configuration areas:
- PostgreSQL connection
- Redis connection
- Anthropic drafting toggle and model
- health, metrics, and tracing settings

Current defaults live in:
- `apps/experience-api/src/main/resources/application.yml`

### decision-worker

Key local configuration areas:
- PostgreSQL connection
- Redis connection
- Kafka bootstrap servers and serializers
- health, metrics, and tracing settings

Current defaults live in:
- `apps/decision-worker/src/main/resources/application.yml`

## API surface

### Case endpoints

Base path:
- `/api/cases`

Current endpoints:
- `GET /api/cases/{caseId}`
- `GET /api/cases/{caseId}/recommendation`
- `GET /api/cases/{caseId}/draft-message`

### OpenAPI

OpenAPI UI is provided by the API application through Springdoc.

When the API is running locally, use:
- `http://localhost:8080/swagger-ui/index.html`

## Build and packaging

### Build service images

```bash
docker build -f apps/experience-api/Dockerfile -t journey-recovery/experience-api:local .
```

```bash
docker build -f apps/decision-worker/Dockerfile -t journey-recovery/decision-worker:local .
```

### Kubernetes manifests

Baseline manifests live in:
- `infra/k8s`

Apply them with:

```bash
kubectl apply -k infra/k8s
```

## Observability

Both services expose:
- liveness and readiness health probes
- Prometheus metrics endpoint
- structured logs on stdout
- Micrometer tracing hooks

Typical local endpoints:
- `/actuator/health/liveness`
- `/actuator/health/readiness`
- `/actuator/prometheus`

## Testing

The project currently uses Gradle-driven automated tests across:
- domain decisioning
- contracts serialization
- API behavior
- worker behavior
- drafting fallback and adapter behavior

Run everything with:

```bash
gradle clean test
```

## Deployment shape

The repository currently supports a baseline containerized deployment path:
- Dockerfiles for both services
- Kubernetes manifests for services and shared dependencies
- CI image builds in GitHub Actions

This is suitable for local validation and a simple deployment pipeline, with room for environment-specific overlays later.
