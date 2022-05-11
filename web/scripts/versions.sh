#!/bin/sh
set -e
cd "${0%/*}"
. ./_variables.sh

cd "$dir_root"
echo "Node version is" "$(node --version)"
echo "Npm version is" "$(npm --version)"
echo "Yarn version is" "$(npx yarn --version)"
echo "App version is" "$(${dir_scripts}/version-app.sh)"
cd "$dir_scripts"
