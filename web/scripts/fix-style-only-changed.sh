#!/bin/sh
set -e
dir_current="${0%/*}"
dir_root="./../.."
dir_scripts="./web/scripts"
cd "$dir_current"

files="$(./git/list-changed-code-files.sh | grep '^web/packages/.*/src/.*' | grep '\.\(js\|ts\|jsx\|tsx\|html\|css\|scss\|sass\|json\)$' | xargs echo)"
if [ "$files" ]; then
  echo "> Fixing with prettier (only changed files)"
  cd "$dir_root"
  npx yarn prettier --no-error-on-unmatched-pattern --write $files
  cd "$dir_scripts"
else
  echo "> Fixing with prettier (only changed files: no changed files found)"
fi
