# Journey Recovery Platform

A Java 17 platform for handling travel disruption cases, computing next best actions, and exposing operational APIs for customer support workflows.

## Modules

- `apps/experience-api`: REST API for case and recommendation reads
- `apps/decision-worker`: event-driven worker for disruption ingestion
- `libs/domain`: core decisioning models and services
- `libs/contracts`: shared DTOs and event contracts
- `libs/testing-support`: shared test helpers

## Getting started

### Prerequisites

- Java 17+
- Gradle 9+
- Docker-compatible container runtime

### Commands

```bash
gradle clean test
```

```bash
gradle bootRun --project-dir apps/experience-api
```

```bash
gradle bootRun --project-dir apps/decision-worker
```

```bash
docker compose -f infra/docker/docker-compose.yml up -d
```

## Milestone 0 scope

- Multi-module Gradle build
- Spring Boot app scaffolding
- Test baseline
- Container stack for Kafka, PostgreSQL, and Redis
- CI workflow and quality gates baseline

## Milestone 1 scope

- Disruption domain models and scoring service
- Shared case and recommendation response contracts
- Seeded read API for cases, recommendations, and message previews
- OpenAPI UI dependency for endpoint inspection

## Milestone 2 scope

- Versioned disruption and recommendation event contracts
- Kafka-driven case ingestion in the decision worker
- PostgreSQL-backed case and segment persistence
- Redis-based duplicate-event protection
- Database-backed read API integration tests

## Milestone 3 scope

- Richer recommendation priority and SLA context
- Cache-backed case reads in the experience API
- Expanded customer context in recommendation scoring
- Focused tests for decisioning, cache snapshots, and draft formatting

## Milestone 4 scope

- Constrained AI-assisted message drafting behind an adapter interface
- Prompt caching on the stable instruction prefix
- Structured draft response parsing with deterministic fallback behavior
- API preview endpoint kept off the core decision path
