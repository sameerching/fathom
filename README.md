# Fathom

Fathom is a personal finance dashboard app that helps users track income, expenses, cards, accounts, investments, liabilities, and net worth.

## Repository Structure

```text
fathom/
  backend/
  frontend/
  docs/
  docker-compose.yml
```

## Prerequisites
- Java 21
- Maven 3.9+
- Node.js 20+
- npm 10+
- Docker + Docker Compose

## Start PostgreSQL

```bash
docker compose up -d postgres
```

## Run Backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend URL: `http://localhost:8080`

Health check:
- `http://localhost:8080/api/health`

## Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend URL: `http://localhost:3000`

## Run Backend Tests

```bash
cd backend
./mvnw test
```

## Phase 2 API Examples

```bash
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"Alice","email":"alice@example.com","status":"ACTIVE"}'

curl -X POST http://localhost:8080/api/users/{userId}/accounts \
  -H 'Content-Type: application/json' \
  -d '{"name":"HDFC Savings","accountType":"BANK_ACCOUNT"}'

curl -X POST http://localhost:8080/api/users/{userId}/transactions \
  -H 'Content-Type: application/json' \
  -d '{"accountId":"{accountId}","transactionDate":"2026-05-01","amount":1250.00,"direction":"DEBIT","transactionType":"EXPENSE","source":"MANUAL"}'

curl -X POST http://localhost:8080/api/users/{userId}/investment-holdings \
  -H 'Content-Type: application/json' \
  -d '{"assetType":"MUTUAL_FUND","name":"Nifty Index Fund","investedAmount":10000.00,"currentValue":10500.00}'

curl -X POST http://localhost:8080/api/users/{userId}/liabilities \
  -H 'Content-Type: application/json' \
  -d '{"liabilityType":"HOME_LOAN","name":"Home Loan","outstandingAmount":2500000.00}'
```
