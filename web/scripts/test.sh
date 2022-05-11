#!/bin/sh
set -e
cd "${0%/*}"
. ./_variables.sh

TestAll () {
  cd "$dir_root"
  files="$(find . -type f | awk '!arr[$1]++' | grep "$packages_regex" | grep -v "node_modules" | grep "$jest_files_regex" | xargs echo)"
  if [ "$files" ]; then
    echo "> Test with jest (whole project)"
    npx yarn jest $files --coverage=false
  else
    echo "> Test with jest (whole project): no changed files found"
  fi
  cd "$dir_scripts"
}

TestAllCiGitlab () {
  cd "$dir_root"
  files="$(find . -type f | awk '!arr[$1]++' | grep "$packages_regex" | grep -v "node_modules" | grep "$jest_files_regex" | xargs echo)"
  if [ "$files" ]; then
    echo "> Test with jest (whole project)"
    npx yarn jest $files --coverage=true
  else
    echo "> Test with jest (whole project): no changed files found"
  fi
  cd "$dir_scripts"
}

TestOnlyChanged () {
  cd "$dir_root"
  files="$(${dir_scripts}/git/list-changed-code-files.sh | grep "$packages_regex" | grep -v "node_modules" | grep "$jest_files_regex" | xargs echo)"
  if [ "$files" ]; then
    echo "> Test with jest (only changed test files)"
    npx yarn jest $files --coverage=false
  else
    echo "> Test with jest (only changed test files): no changed test files found"
  fi
  cd "$dir_scripts"
}

TestOnlyStaged () {
  cd "$dir_root"
  files="$(${dir_scripts}/git/list-staged-code-files.sh | grep "$packages_regex" | grep -v "node_modules" | grep "$jest_files_regex" | xargs echo)"
  if [ "$files" ]; then
    echo "> Test with jest (only staged test files)"
    npx yarn jest $files --coverage=false
  else
    echo "> Test with jest (only staged test files): no staged test files found"
  fi
  cd "$dir_scripts"
}

echo "> Test"
if [ "$1" = "staged" ]; then
  TestOnlyStaged
elif [ "$1" = "changed" ]; then
  TestOnlyChanged
elif [ "$1" = "ci" ]; then
  TestAllCiGitlab
else
  TestAll
fi
echo "> Test done"
