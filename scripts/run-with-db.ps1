$env:DATABASE_URL = 'jdbc:postgresql://ep-spring-smoke-a1rurrk9-pooler.ap-southeast-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_m8bSEpr3wIeM&sslmode=require&channelBinding=require'
Write-Host "Starting bootRun with DATABASE_URL from script..."
./gradlew bootRun --no-daemon --console=plain
