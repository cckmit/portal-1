#!/bin/sh
set -e
dir_root="./../.."
dir_scripts="./web/scripts"
cd "${0%/*}"

cd "$dir_root"
npx yarn workspace @protei-portal/app-portal run devwatch --progress --no-color
cd "$dir_scripts"
