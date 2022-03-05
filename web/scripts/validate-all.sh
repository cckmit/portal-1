#!/bin/sh
set -e
dir_root="./../.."
dir_scripts="./web/scripts"
cd "${0%/*}"

echo "> Validating with eslint (whole project)"
cd "$dir_root"
npx yarn eslint --format unix --ignore-pattern 'node_modules/*' web/packages
cd "$dir_scripts"

echo "> Validating with prettier (whole project)"
cd "$dir_root"
npx yarn prettier --check web/packages
cd "$dir_scripts"

echo "> Validating with tsc (whole project)"
cd "$dir_root"
npx tsc --noEmit
cd "$dir_scripts"
