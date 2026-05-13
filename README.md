# Fathom

Fathom is a personal finance dashboard app that will help users track income, expenses, credit card spends, bank transactions, investments, liabilities, net worth, and monthly cash flow.

This repository currently contains **Phase 1 foundation setup only**.

## Repository Structure

```text
fathom/
  backend/
  frontend/
  docs/
  docker-compose.yml
  README.md
```

## Prerequisites
- Java 21
- Maven 3.9+
- Node.js 20+
- npm 10+
- Docker + Docker Compose

## Start PostgreSQL

From repository root:

```bash
docker compose up -d postgres
```

Stop database:

```bash
docker compose down
```

## Run Backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs on: `http://localhost:8080`

Health check URL:

- `http://localhost:8080/api/health`

Expected response:

```json
{
  "status": "UP",
  "app": "Fathom"
}
```

## Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on: `http://localhost:3000`

## Phase 1 Notes
- No authentication implementation yet.
- No real bank integrations yet.
- No bank credential storage.
- No transaction upload or investment calculation logic yet.
