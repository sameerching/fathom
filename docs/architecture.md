# Fathom Architecture (Phase 1)

## High-Level Architecture
Fathom uses a monorepo with two independently runnable applications:
- **Backend**: Java 21 + Spring Boot REST API using Maven.
- **Frontend**: Next.js + TypeScript web app.
- **Database**: PostgreSQL running in Docker Compose for local development.

## Backend Modules
Base package: `com.fathom`
- `common`: shared utilities and cross-cutting concerns.
- `health`: service health endpoints.
- `user`: future user profiles and settings.
- `account`: future financial account domain.
- `transaction`: future transactions and ledger flows.
- `category`: future spending and income categories.
- `investment`: future holdings and valuation models.
- `liability`: future debts and obligations.
- `dashboard`: future summary aggregations.
- `upload`: future import and ingestion flow.

Only the `health` module is implemented in Phase 1.

## Frontend Pages
- `/` Landing page with product title, subtitle, and navigation placeholders:
  - Dashboard
  - Transactions
  - Upload
  - Investments
  - Liabilities
  - Net Worth

## Database Choice
PostgreSQL is selected for reliability, transactional consistency, and strong Spring Data JPA support.

## Future Integration Ideas
- Secure user authentication and authorization.
- Bank account and credit card aggregation integrations.
- Transaction upload and categorization workflows.
- Investment data refresh and portfolio analytics.
- Dashboard insights for net worth and monthly cash flow.
