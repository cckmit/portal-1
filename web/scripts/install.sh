#!/bin/sh
set -e
dir_root="./../.."
dir_scripts="./web/scripts"
cd "${0%/*}"

cd "$dir_root"
npx yarn install
cd "$dir_scripts"
