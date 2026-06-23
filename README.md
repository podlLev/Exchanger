<h1 align="center">Exchanger</h1>

<p align="center">
  A Spring Boot currency exchange platform with wallet management, live bank rates, and a Telegram bot for authentication and transaction approval.
</p>

## Overview

Exchanger is a backend service for holding multi-currency wallets and exchanging or transferring money between users. Exchange rates are pulled automatically from the PrivatBank public API on a schedule, stored in PostgreSQL, and cached in Redis for fast access. Account activation and sensitive transaction approvals (like outgoing transfers) are handled through a companion Telegram bot, so every user is tied to a verified chat for confirmation.

## Features

- **Multi-currency wallets** — deposit (`PUT`), withdraw (`GET`), exchange (`EXCH`), and transfer (`TRANSF`) money across UAH, USD, and EUR
- **Transfer approval flow** — outgoing transfers are created as `PENDING` and must be approved before funds move, tracked via `TransactionStatus` (`PENDING` / `EXECUTED` / `FAILED`)
- **Live exchange rates** — rates are fetched from the PrivatBank public API on a configurable cron schedule and upserted into PostgreSQL
- **Redis caching** — frequently accessed data (e.g. rates) is cached with a 5-minute TTL to reduce database load
- **Telegram bot integration** — users activate their account by messaging the bot with their registered email, linking their account to a Telegram chat ID for notifications and approvals
- **REST API** — endpoints for users, wallets, and rates, documented with OpenAPI/Swagger
- **Database migrations** — schema is version-controlled and applied automatically via Liquibase

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4 (Web, Data JPA, Validation, Cache) |
| Database | PostgreSQL 15 |
| Migrations | Liquibase |
| Caching | Redis 7 (Spring Cache) |
| Mapping | MapStruct |
| Messaging | Telegram Bots API (`telegrambots-spring-boot-starter`) |
| API docs | springdoc-openapi (Swagger UI) |
| Build tool | Maven |
| Testing | JUnit, Spring Boot Test, LogCaptor, JaCoCo (coverage) |
| Containerization | Docker, Docker Compose |
| External API | [PrivatBank Public API](https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=5) (exchange rates) |

## Prerequisites

- Docker and Docker Compose (for the quickest setup)
- Java 17+ and Maven (only needed if running the app outside Docker)
- A [Telegram bot token](https://core.telegram.org/bots#how-do-i-create-a-bot) (create one via [@BotFather](https://t.me/BotFather))

## Getting Started

### Option A: Run with Docker (quickest)

This runs the app, PostgreSQL, and Redis together in containers:

```bash
docker compose up -d
```

This builds the app image locally, applies Liquibase migrations automatically on startup, and wires up all three services. You'll need a `.env` file in the project root first — see step 2 below.

### Option B: Clone and run it yourself

#### 1. Clone the repository

```bash
git clone https://github.com/podlLev/Exchanger.git
cd Exchanger
```

#### 2. Configure environment variables

Create a `.env` file in the project root:

```env
POSTGRES_DB=exchanger
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/exchanger?useUnicode=true&serverTimezone=UTC&createDatabaseIfNotExist=true
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui.html

SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
SPRING_CACHE_TYPE=redis

BANK_RATE_URL=https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=5
SCHEDULE_CRON_TIME_TABLE=0 0 * * * MON-FRI

TELEGRAM_BOT_USERNAME=your_bot_username
TELEGRAM_BOT_TOKEN=your_telegram_bot_token
```

> **Never commit your real `.env` file or bot token.** Add `.env` to `.gitignore` and rotate any token that has previously been pushed to a public repo.

#### 3. Start dependencies (PostgreSQL + Redis)

```bash
docker compose -f docker-compose.dev.yml up -d
```

This starts PostgreSQL on port `5432` and Redis on port `6379`, matching the `.env` values above.

#### 4. Run the application

```bash
mvn spring-boot:run
```

The app will be available at **http://localhost:8080**.

## API Documentation

Once running, interactive API docs are available at:

```
http://localhost:8080/swagger-ui.html
```

REST endpoints are namespaced under `/api/v1/` (e.g. `/api/v1/wallets/exchange`, `/api/v1/rates`, `/api/v1/users`).

## Telegram Bot Setup

1. Create a bot via [@BotFather](https://t.me/BotFather) and copy its username and token into `TELEGRAM_BOT_USERNAME` / `TELEGRAM_BOT_TOKEN` in your `.env`.
2. Once the app is running, message your bot `/start`.
3. Reply with the email address used to register your account — this links your Telegram chat to your user record and activates the account.

## Running Tests

```bash
mvn test
```

The suite covers controllers, services, mappers, DTOs, exceptions, and the Telegram bot logic. JaCoCo generates a coverage report at `target/site/jacoco/index.html` after running tests.

## Project Structure

```
src/main/java/com/exchanger/
├── config/          # Redis cache configuration
├── controller/       # REST controllers (rates, users, wallets)
├── dto/              # Data transfer objects (transactions, transfers, user records)
├── model/            # JPA entities (User, Wallet, Rate, Transaction) and enums
├── repository/       # Spring Data JPA repositories
├── service/          # Business logic interfaces + implementations
├── telegram/         # Telegram bot, config, and message utilities
├── mapper/           # MapStruct entity↔DTO mappers
└── exception/        # Custom exceptions and global exception handling

src/main/resources/
└── liquibase/        # Database changelogs
```
