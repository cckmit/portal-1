#!/bin/sh
set -e
dir_current="${0%/*}"
dir_root="./../.."
dir_scripts="./web/scripts"
cd "$dir_current"

files="$(./git/list-staged-code-files.sh | grep '^web/packages/.*/src/.*' | grep '\.\(js\|ts\|jsx\|tsx\)$' | xargs echo)"
if [ "$files" ]; then
  echo "> Validating with eslint (only staged files)"
  cd "$dir_root"
  npx yarn eslint --format unix --ignore-pattern 'node_modules/*' $files
  cd "$dir_scripts"
else
  echo "> Validating with eslint (only staged files): no staged files found"
fi

files="$(./git/list-staged-code-files.sh | grep '^web/packages/.*/src/.*' | grep '\.\(js\|ts\|jsx\|tsx\|html\|css\|scss\|sass\|json\)$' | xargs echo)"
if [ "$files" ]; then
  echo "> Validating with prettier (only staged files)"
  cd "$dir_root"
  npx yarn prettier --no-error-on-unmatched-pattern --check $files
  cd "$dir_scripts"
else
  echo "> Validating with prettier (only staged files): no staged files found"
fi

echo "> Validating with tsc (whole project)"
cd "$dir_root"
npx tsc --noEmit
cd "$dir_scripts"
