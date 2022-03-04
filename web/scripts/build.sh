#!/bin/sh
set -e

echo "> Build"

echo "> List versions"
./scripts/versions.sh

echo "> Install project"
npx yarn install --immutable

echo "> Build '@protei-portal/app-portal'"
npx yarn workspace @protei-portal/app-portal run build --no-color

echo "> Build done"
