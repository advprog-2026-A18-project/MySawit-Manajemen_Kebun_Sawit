# Neon Setup Guide

This project is configured to connect to Neon Serverless Postgres for the MySawit application.

## Prerequisites

- **Neon Organization**: `org-sweet-base-56297184`
- **Neon Project**: `divine-violet-18558553`
- PostgreSQL JDBC driver (already added to `build.gradle.kts`)
- Spring Data JPA (already added to `build.gradle.kts`)

## Step 1: Get Your Connection String

### Option A: Using Neon Console

1. Navigate to the [Neon Console](https://console.neon.tech)
2. Select your organization: `org-sweet-base-56297184`
3. Select your project: `divine-violet-18558553`
4. Click **Connect** on the Project Dashboard
5. Select **Java** as the language
6. Copy the full JDBC connection string (it will look like):
   ```
   postgresql://[user]:[password]@[hostname]/[dbname]
   ```

### Option B: Using Neon API

You can also retrieve the connection file programmatically:

```bash
# Set your Neon API key
export NEON_API_KEY="your-neon-api-key"

# Get project details
curl -H "Authorization: Bearer $NEON_API_KEY" \
  "https://console.neon.tech/api/v2/projects/divine-violet-18558553"
```

## Step 2: Configure Local Environment

1. **Update the `.env` file** with your connection string:

   ```env
   DATABASE_URL=postgresql://user:password@ep-XXXXX-XXXXX.us-east-2.aws.neon.tech/neondb
   ```

2. **Important**: Never commit `.env` file to version control. Update `.gitignore`:

   ```
   .env
   ```

## Step 3: Update application.properties for Different Environments

### Development Environment

The `src/main/resources/application.properties` is already configured to use the `DATABASE_URL` environment variable.

### Production Environment

Create `src/main/resources/application-prod.properties`:

```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

## Step 4: Build and Run

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

## Connection String Format

Your Neon connection string format (JDBC):

```
jdbc:postgresql://[hostnanme]/[dbname]?user=[user]&password=[password]&sslmode=require&channelBinding=require
```

**Connection String Components:**
- `hostname`: Your Neon endpoint hostname (e.g., `ep-cool-darkness-123456.us-east-2.aws.neon.tech`)
- `dbname`: Database name (defaults to `neondb`)
- `user`: Database user (defaults to `neondb_owner`)
- `password`: Your database password (securely stored in `.env`)

## Neon Features Available

✅ **Autoscaling**: Compute automatically scales based on load
✅ **Scale to Zero**: Pay only for what you use
✅ **Branching**: Create isolated database branches for development
✅ **Connection Pooling**: Built-in PgBouncer (add `-pooler` to hostname for pooled connections)
✅ **Point-in-Time Recovery**: Restore to any point in the past

## Using Connection Pooling (Recommended for Web Apps)

For web applications, use the pooled connection endpoint:

```properties
# Add "-pooler" to your endpoint hostname
spring.datasource.url=postgresql://user:password@ep-cool-darkness-123456-pooler.us-east-2.aws.neon.tech/neondb
```

## Database Migrations

To manage schema migrations with Neon and Spring Boot, consider using:

- **Flyway**: Recommended for Spring Boot applications
- **Liquibase**: Alternative migration tool

Example adding Flyway:

```gradle
implementation("org.flywaydb:flyway-core")
```

Then create migration files in `src/main/resources/db/migration/`.

## Troubleshooting

### Connection Timeout
- Ensure your IP is allowed in Neon's IP allowlist
- Check that your `.env` file has the correct connection string
- Verify the database is running: `psql -c "SELECT 1;" [connection_string]`

### SSL/TLS Issues
- Ensure `sslmode=require` is in your connection string
- The PostgreSQL JDBC driver handles this automatically

### Max Connections
- Neon limits concurrent connections based on compute size
- Use connection pooling (add `-pooler` to hostname) for web applications
- Default pool size is 90% of `max_connections`

## Additional Resources

- [Neon Documentation](https://neon.com/docs)
- [Java with Neon Guide](https://neon.com/docs/guides/java)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)

## Next Steps

1. Obtain your connection string from the Neon Console
2. Update the `.env` file with your credentials
3. Verify the connection works: `./gradlew bootRun`
4. Start building your data entities with Spring Data JPA

---

**Organization ID**: `org-sweet-base-56297184`
**Project ID**: `divine-violet-18558553`
