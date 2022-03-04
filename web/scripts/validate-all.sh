#!/bin/sh
set -e

echo "> Validating with eslint (whole project)"
npx yarn eslint --format unix --ignore-pattern 'node_modules/*' packages

echo "> Validating with prettier (whole project)"
npx yarn prettier --check packages

echo "> Validating with tsc (whole project)"
npx tsc --noEmit
