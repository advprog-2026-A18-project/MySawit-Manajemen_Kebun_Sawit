#!/bin/bash
# Script to fetch Neon project connection details
# Usage: ./scripts/get-neon-connection.sh <API_KEY>

set -e

API_KEY="${1:-}"
ORG_ID="org-sweet-base-56297184"
PROJECT_ID="divine-violet-18558553"

if [ -z "$API_KEY" ]; then
    echo "Error: Please provide your Neon API key as an argument"
    echo "Usage: ./scripts/get-neon-connection.sh <NEON_API_KEY>"
    echo ""
    echo "To get your API key:"
    echo "1. Visit https://console.neon.tech"
    echo "2. Go to Account Settings > API Keys"
    echo "3. Create a new API key"
    exit 1
fi

echo "Fetching connection details for:"
echo "  Organization: $ORG_ID"
echo "  Project: $PROJECT_ID"
echo ""

# Fetch project details
RESPONSE=$(curl -s -H "Authorization: Bearer $API_KEY" \
  "https://console.neon.tech/api/v2/projects/$PROJECT_ID")

# Check if request was successful
if echo "$RESPONSE" | grep -q '"message":"Unauthorized"'; then
    echo "Error: Invalid API key. Please check your credentials."
    exit 1
fi

if echo "$RESPONSE" | grep -q '"error"'; then
    echo "Error: Failed to fetch project details"
    echo "$RESPONSE" | jq .
    exit 1
fi

# Extract connection information
echo "Project Details:"
echo "$RESPONSE" | jq '.project | {name, id, region_id, pg_version}' 2>/dev/null || echo "Failed to parse response"

# Try to get connection URI for primary branch
echo ""
echo "To get your full connection string:"
echo "1. Visit https://console.neon.tech/app/projects/$PROJECT_ID"
echo "2. Click 'Connect' on the dashboard"
echo "3. Select 'Java' as the language"
echo "4. Copy the connection string"
echo ""
echo "Then update your .env file with:"
echo "  DATABASE_URL=<paste_your_connection_string_here>"
