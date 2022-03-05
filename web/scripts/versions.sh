#!/bin/sh
set -e
dir_current="${0%/*}"
dir_root="./../.."
dir_scripts="./web/scripts"
cd "$dir_current"

cd "$dir_root"
echo "Node version is" "$(node --version)"
echo "Npm version is" "$(npm --version)"
echo "Yarn version is" "$(npx yarn --version)"
cd "$dir_scripts"
