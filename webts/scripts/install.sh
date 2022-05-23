#!/bin/sh
set -e
cd "${0%/*}"
. ./_variables.sh

cd "$dir_root"
npx yarn install
cd "$dir_scripts"
