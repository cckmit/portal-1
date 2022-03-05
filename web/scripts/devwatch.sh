#!/bin/sh
set -e
dir_current="${0%/*}"
dir_root="./../.."
dir_scripts="./web/scripts"
cd "$dir_current"

cd "$dir_root"
npx yarn workspace @protei-portal/app-portal run devwatch --progress --no-color
cd "$dir_scripts"
