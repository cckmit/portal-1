#!/bin/sh
set -e
dir_current="${0%/*}"
dir_root="./../.."
dir_scripts="./web/scripts"
cd "$dir_current"

echo "> Build"

echo "> List versions"
./versions.sh

echo "> Install project"
cd "$dir_root"
npx yarn install --immutable
cd "$dir_scripts"

echo "> Build '@protei-portal/app-portal'"
cd "$dir_root"
npx yarn workspace @protei-portal/app-portal run build --no-color
cd "$dir_scripts"

echo "> Build done"
