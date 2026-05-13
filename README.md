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

## Phase 3: CSV Transaction Import (MVP)

Normalized CSV headers:
- Required: `transactionDate,direction,amount,rawDescription,transactionType`
- Optional: `merchant,categoryName,notes`

Example:
```csv
transactionDate,direction,amount,rawDescription,merchant,transactionType,categoryName,notes
2026-05-01,DEBIT,1250.00,Swiggy order,Swiggy,EXPENSE,Food,Dinner
2026-05-02,CREDIT,850000.00,Salary credited,Employer,INCOME,Salary,Monthly salary
```

Upload command:
```bash
curl -X POST "http://localhost:8080/api/users/{userId}/accounts/{accountId}/transaction-imports?source=MANUAL" \
  -F "file=@transactions.csv"
```

Duplicate detection is hash-based on `userId + accountId + transactionDate + direction + amount + normalized(rawDescription)`; duplicates are skipped during import.

Out of scope in Phase 3: bank-specific CSV parsers and provider integrations.

## Phase 4: Dashboard & Filtering APIs

Filtered transaction listing:
```bash
curl "http://localhost:8080/api/users/{userId}/transactions?from=2026-05-01&to=2026-05-31&merchant=amazon&minAmount=100"
```

Monthly summary:
```bash
curl "http://localhost:8080/api/users/{userId}/dashboard/monthly-summary?month=2026-05"
```

Category breakdown:
```bash
curl "http://localhost:8080/api/users/{userId}/dashboard/category-breakdown?from=2026-05-01&to=2026-05-31&type=EXPENSE"
```

Net worth summary:
```bash
curl "http://localhost:8080/api/users/{userId}/dashboard/net-worth"
```

## Phase 6: Frontend Setup & Onboarding

1. Copy frontend environment file:
```bash
cd frontend
cp .env.example .env.local
```
2. Run backend (`http://localhost:8080`) and frontend (`http://localhost:3000`).
3. Open `http://localhost:3000/setup`.
4. Create a user in **Section A** (user ID is saved to localStorage).
5. Create an account in **Section B**.
6. Open `/upload` and import a normalized CSV.
7. Review `/dashboard` and `/transactions`.

Optional advanced flow (API-first):
```bash
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"Alice","email":"alice@example.com","status":"ACTIVE"}'

curl -X POST http://localhost:8080/api/users/{userId}/accounts \
  -H 'Content-Type: application/json' \
  -d '{"name":"Primary Account","accountType":"BANK_ACCOUNT"}'
```
