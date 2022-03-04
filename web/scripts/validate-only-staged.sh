#!/bin/sh
set -e

files="$(./scripts/git/list-staged-code-files.sh | grep '^web/packages/.*/src/.*' | grep '\.\(js\|ts\|jsx\|tsx\)$' | xargs echo)"
if [ "$files" ]; then
  echo "> Validating with eslint (only staged files)"
  npx yarn eslint --format unix --ignore-pattern 'node_modules/*' $files
else
  echo "> Validating with eslint (only staged files): no staged files found"
fi

files="$(./scripts/git/list-staged-code-files.sh | grep '^web/packages/.*/src/.*' | grep '\.\(js\|ts\|jsx\|tsx\|html\|css\|scss\|sass\|json\)$' | xargs echo)"
if [ "$files" ]; then
  echo "> Validating with prettier (only staged files)"
  npx yarn prettier --no-error-on-unmatched-pattern --check $files
else
  echo "> Validating with prettier (only staged files): no staged files found"
fi

echo "> Validating with tsc (whole project)"
npx tsc --noEmit
