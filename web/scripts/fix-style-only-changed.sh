#!/bin/sh
set -e
cd "${0%/*}"
. ./_variables.sh

FixOnlyChangedByPrettier () {
  cd "$dir_root"
  files="$(${dir_scripts}/git/list-changed-code-files.sh | grep "$packages_regex" | grep -v "node_modules" | grep "$prettier_files_regex" | xargs echo)"
  if [ "$files" ]; then
    echo "> Fixing with prettier (only changed files)"
    npx yarn prettier --no-error-on-unmatched-pattern --write $files
  else
    echo "> Fixing with prettier (only changed files: no changed files found)"
  fi
  cd "$dir_scripts"
}

FixOnlyChangedByPrettier
