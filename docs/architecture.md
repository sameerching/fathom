# Fathom Architecture (Phase 2)

## High-Level Architecture
- Backend: Spring Boot 3 (Java 21), REST APIs, JPA, Flyway.
- Frontend: Next.js.
- Database: PostgreSQL (dev), H2 (tests, PostgreSQL compatibility mode).

## Backend Domain Packages
- `com.fathom.common`: base entity auditing (`created_at`, `updated_at`), error handling.
- `com.fathom.user`: `AppUser` domain and APIs.
- `com.fathom.account`: `FinancialAccount` domain and APIs.
- `com.fathom.category`: `Category` domain and APIs.
- `com.fathom.transaction`: `Transaction` ledger domain and APIs.
- `com.fathom.investment`: `InvestmentHolding` domain and APIs.
- `com.fathom.liability`: `Liability` domain and APIs.

## Data Model (Core)
- UUID primary keys for all entities.
- BigDecimal for money fields.
- LocalDate for value/transaction dates.
- Instant for audit timestamps.
- Enums for stable domain values.

## Migration Strategy
- Flyway migration: `V1__create_core_schema.sql`
- Hibernate DDL mode remains `validate`.
