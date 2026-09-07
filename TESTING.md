# Testing Guide

This project uses JUnit 5, Mockito and Spring MockMvc for automated tests.
The suite is database-independent: service tests mock MyBatis mappers, and
controller tests run against standalone MockMvc. No local MySQL instance is
required to run the tests.

## Test scope

- AES encryption / decryption for sensitive financial fields
- MyBatis encrypted BigDecimal type handler
- JWT token generation, validation, expiry and tamper detection
- Current-user helpers backed by Spring Security context
- User registration, login, password reset and password update rules
- Evaluation score authorization rules (self, manager and peer flows)
- Weighted performance score calculation and final grade mapping
- Salary adjustment generation rules by grade
- Performance goal deletion and KPI binding rules
- KPI indicator duplicate-name rules
- Appeal submission and handling state rules
- Authentication controller API responses and bean validation errors

## Commands

Run the complete backend suite:

```bash
mvn -B test
```

Run a single test class:

```bash
mvn -B test -Dtest=JwtTokenProviderTest
```

The same suite runs on every push and pull request through
`.github/workflows/ci.yml`.
