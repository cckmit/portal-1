#!/bin/sh
set -e
cd "${0%/*}"
. ./_variables.sh
. ./git/is-git.sh
. ./node/is-node.sh

RunJest () {
  files="$(cat | grep "$packages_regex" | grep -v "node_modules" | grep "$jest_files_regex" | xargs echo)"
  prefix=$1
  as_ci=$2
  coverage=false
  if [[ $as_ci == true ]]; then coverage=true; fi
  if [ "$files" ]; then
    echo "> Test with jest ($prefix)"
    cd "$dir_web"
    npx yarn jest $files --coverage=$coverage --config ./jest.config.js --coverageDirectory "$dir_root_from_web"/build/coverage
    cd "$dir_root_from_web"
  else
    echo "> Test with jest ($prefix): no files found"
  fi
}

die () {
  echo "> Test failed"
  exit 1
}

echo "> Test"
if ! IsNodeVersion 16 ; then echo ">! Required node 16 to test this app"; die; fi
cd "$dir_root"
if [ "$1" = "staged" ]; then
  if ! IsGit ; then echo ">! Required git to test this app"; die; fi
  ${dir_scripts}/git/list-staged-code-files.sh | RunJest "only staged test files" || die
elif [ "$1" = "changed" ]; then
  if ! IsGit ; then echo ">! Required git to test this app"; die; fi
  ${dir_scripts}/git/list-changed-code-files.sh | RunJest "only changed test files" || die
elif [ "$1" = "ci" ]; then
  ${dir_scripts}/git/list-code-files.sh | RunJest "whole project" true || die
else
  ${dir_scripts}/git/list-code-files.sh | RunJest "whole project" || die
fi
cd "$dir_scripts"
echo "> Test done"
