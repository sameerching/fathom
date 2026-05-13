# Fathom

Fathom is a personal finance dashboard app.

## Run backend tests
```bash
cd backend
./mvnw test
```

## Example API calls
```bash
curl -X POST http://localhost:8080/api/users -H 'Content-Type: application/json' -d '{"name":"Alice","email":"alice@example.com","status":"ACTIVE"}'

curl -X POST http://localhost:8080/api/users/{userId}/accounts -H 'Content-Type: application/json' -d '{"name":"HDFC Savings","accountType":"BANK_ACCOUNT"}'

curl -X POST http://localhost:8080/api/users/{userId}/transactions -H 'Content-Type: application/json' -d '{"accountId":"{accountId}","transactionDate":"2026-05-01","amount":1250.00,"direction":"DEBIT","transactionType":"EXPENSE","source":"MANUAL"}'
```
