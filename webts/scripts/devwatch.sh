#!/bin/sh
set -e
cd "${0%/*}"
. ./_variables.sh

cd "$dir_root"
npx yarn workspace @protei-portal/app-portal run devwatch --progress --no-color
cd "$dir_scripts"
