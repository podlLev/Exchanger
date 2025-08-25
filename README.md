# Exchanger

A Spring Boot project for fetching and exchanging currencies with support for a Telegram bot, Redis, and PostgreSQL.  
The project automatically retrieves exchange rates from the bank API (PrivatBank), stores them in PostgreSQL, caches the latest values in Redis for fast access, and provides a Telegram bot for user authentication and secure transactions.

---

## Features
- Real-time currency rates via PrivatBank API
- Data storage in PostgreSQL
- Caching using Redis
- Telegram bot for authentication and transaction verification
- Swagger UI for API testing
- Liquibase for database schema management

---

## Technologies
- Java 17, Spring Boot 3
- PostgreSQL 15
- Redis 7
- Docker / Docker Compose
- Swagger

---

## Installation & Run

### Clone the repository
```bash
git clone <https://github.com/podlLev/Exchanger.git>
cd exchanger
```

## Run with Docker

```bash
# For production (Prod)
docker-compose up -d

# For development (Dev)
docker-compose -f docker-compose.dev.yml up --build
```
