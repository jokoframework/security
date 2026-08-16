# Joko Security - Flyway Migration Templates

This folder contains Flyway SQL migration templates for the **joko-security** library. These templates create the database schema required for JWT-based authentication and authorization.

## 📁 Available Templates

### Core Migrations (Required)

1. **V1__create_joko_security_schema.sql.template**
   - Creates the `joko_security` schema
   - Includes quotes for H2 compatibility

2. **V2__create_joko_security_tables.sql.template**
   - Creates all core tables:
     - `consumer_api` - API consumer registry
     - `keychain` - JWT signing secret storage
     - `principal_session` - User session tracking
     - `audit_session` - Session audit logs
     - `security_profile` - Token lifespan configurations
     - `seed` - OTP/TOTP seeds for two-factor authentication
     - `tokens` - Active refresh tokens

3. **V3__create_joko_security_indexes.sql.template**
   - Creates indexes for better query performance
   - Adds unique constraints on critical fields

### Additional Migrations (Optional)

For development/testing, you may want to add:

- **V4__seed_development_data.sql** - Basic security profiles and test data
- **V5__seed_additional_test_data.sql** - Consumer API and OTP seed data

See `development/src/main/resources/db/migration/` for examples.

## 🚀 Usage

### 1. Copy Templates to Your Project

```bash
# Copy to your project's migration folder
cp database-templates/flyway/sql/*.template your-project/src/main/resources/db/migration/

# Remove .template extension
cd your-project/src/main/resources/db/migration/
rename 's/\.template$//' *.template
```

### 2. Configure Flyway

**For H2 (Development):**

```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

spring.datasource.url=jdbc:h2:mem:app_db
spring.datasource.driver-class-name=org.h2.Driver
```

**For PostgreSQL (Production):**

```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.driver-class-name=org.postgresql.Driver
```

### 3. Run Migrations

Migrations run automatically when the Spring Boot application starts with Flyway enabled.

**Manual execution (if needed):**

```bash
mvn flyway:migrate
```

## 🗄️ Database Schema Overview

### Core Tables

| Table | Purpose |
|-------|---------|
| `security_profile` | Defines token timeout configurations (access/refresh token lifespans) |
| `keychain` | Stores JWT signing secret (if using DB mode) |
| `tokens` | Active refresh tokens with metadata |
| `principal_session` | User session records |
| `audit_session` | Login audit trail |
| `seed` | OTP/TOTP seeds for two-factor authentication |
| `consumer_api` | External API consumer credentials |

### Key Relationships

- `tokens.security_profile_id` → `security_profile.id`
- `audit_session.id_principal` → `principal_session.id`

## 📝 Important Notes

### H2 vs PostgreSQL

The templates use **quoted identifiers** (`"joko_security"`) for H2 compatibility:
- H2 converts unquoted names to uppercase
- PostgreSQL converts to lowercase
- Quotes preserve the exact case

If using **PostgreSQL only**, you can remove quotes (but it's not required).

### Schema Name

All tables are created in the `joko_security` schema. To use a different schema:
1. Edit the templates
2. Replace `"joko_security"` with your desired schema name
3. Update your application configuration accordingly

### BIGSERIAL vs SERIAL

The templates use `BIGSERIAL` for primary keys:
- PostgreSQL: Native support
- H2: Automatically maps to `BIGINT AUTO_INCREMENT`

### Seed Data

The templates **DO NOT** include seed data. For development:
- Copy `V4__seed_development_data.sql` from the `development/` project
- This includes test security profiles and keychain
- Modify as needed for your environment

## 🔗 References

- **Working Example**: See `development/src/main/resources/db/migration/` for a complete working setup
- **Liquibase Alternative**: See `src/main/resources/db/liquibase/` for Liquibase changesets
- **Flyway Documentation**: https://flywaydb.org/documentation/
- **Joko Security Docs**: See `README.md` in the project root

## 🔧 Troubleshooting

### Migration fails with "schema not found"

Ensure V1 runs first and creates the schema before V2.

### "Table already exists" error

If migrating an existing database with Liquibase, you may have conflicts. Consider:
- Using Liquibase exclusively (disable Flyway)
- Or migrate Liquibase history to Flyway baseline

### H2 case sensitivity issues

Always use quoted identifiers in H2: `"joko_security"` not `joko_security`

## 📦 Version Compatibility

These templates are compatible with:
- **Spring Boot**: 3.3.1+
- **Flyway**: 10.21.0+
- **PostgreSQL**: 9.4+
- **H2**: 2.x (in-memory and file-based)
- **Java**: 17+

---

**Last Updated**: December 2024
**Based on**: joko-security v1.2.17 (Spring Boot 3 migration)
