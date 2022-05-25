#!/bin/sh
set -e
cd "${0%/*}"
. ./_variables.sh

cd "$dir_root"
sed -n '2s/"version": "//p' "$dir_web"/version.json \
 | sed -n 's/",//p' \
 | sed -n 's/ //gp'
cd "$dir_scripts"
