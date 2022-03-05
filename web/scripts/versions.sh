#!/bin/sh
set -e
dir_root="./../.."
dir_scripts="./web/scripts"
cd "${0%/*}"

cd "$dir_root"
echo "Node version is" "$(node --version)"
echo "Npm version is" "$(npm --version)"
echo "Yarn version is" "$(npx yarn --version)"
cd "$dir_scripts"
