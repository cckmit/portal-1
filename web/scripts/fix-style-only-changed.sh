#!/bin/sh
set -e

files="$(./scripts/git/list-changed-code-files.sh | grep '^web/packages/.*/src/.*' | grep '\.\(js\|ts\|jsx\|tsx\|html\|css\|scss\|sass\|json\)$' | xargs echo)"
if [ "$files" ]; then
  echo "> Fixing with prettier (only changed files)"
  npx yarn prettier --no-error-on-unmatched-pattern --write $files
else
  echo "> Fixing with prettier (only changed files: no changed files found)"
fi
