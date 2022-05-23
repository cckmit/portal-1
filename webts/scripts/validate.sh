#!/bin/sh
set -e
cd "${0%/*}"
. ./_variables.sh
. ./git/is-git.sh
. ./node/is-node.sh

RunEslint () {
  files="$(cat | grep "$packages_regex" | grep -v "node_modules" | grep "$eslint_files_regex" | xargs echo)"
  prefix=$1
  as_ci=$2
  if [ "$files" ]; then
    echo "> Validating with eslint ($prefix)"
    if [ $as_ci ]; then
      if [ ! -d "./build" ]; then mkdir "build"; fi
      ESLINT_FORMAT=stylish \
      ESLINT_HTML_REPORT=./build/code-quality-report.html \
      ESLINT_CODE_QUALITY_REPORT=./build/code-quality-report-gitlab.json \
      npx yarn eslint --format "$dir_scripts"/eslint/eslint-formatter-gitlab.js --ignore-pattern 'node_modules/*' $files
    else
      npx yarn eslint --format stylish --ignore-pattern 'node_modules/*' $files
    fi
  else
    echo "> Validating with eslint ($prefix): no files found"
  fi
}

RunPrettier () {
  files="$(cat | grep "$packages_regex" | grep -v "node_modules" | grep "$prettier_files_regex" | xargs echo)"
  prefix=$1
  if [ "$files" ]; then
    echo "> Validating with prettier ($prefix)"
    npx yarn prettier --no-error-on-unmatched-pattern --check $files
  else
    echo "> Validating with prettier ($prefix): no files found"
  fi
}

RunTypescriptCompiler () {
  files="$(cat | grep "$packages_regex" | grep -v "node_modules" | xargs echo)"
  prefix=$1
  if [ "$files" ]; then
    echo "> Validating with tsc ($prefix)"
    npx tsc --noEmit
  else
    echo "> Validating with tsc ($prefix): no files found"
  fi
}

die () {
  echo "> Validate failed"
  exit 1
}

echo "> Validate"
if ! IsGit ; then echo ">! Required git to validate this app"; die; fi
if ! IsNodeVersion 16 ; then echo ">! Required node 16 to validate this app"; die; fi
cd "$dir_root"
if [ "$1" = "staged" ]; then
  ${dir_scripts}/git/list-staged-code-files.sh | RunEslint "only staged files" || die
  ${dir_scripts}/git/list-staged-code-files.sh | RunTypescriptCompiler "only staged files" || die
elif [ "$1" = "changed" ]; then
  ${dir_scripts}/git/list-changed-code-files.sh | RunEslint "only changed files" || die
  ${dir_scripts}/git/list-changed-code-files.sh | RunTypescriptCompiler "only changed files" || die
elif [ "$1" = "ci" ]; then
  ${dir_scripts}/git/list-code-files.sh | RunEslint "whole project" true || die
  ${dir_scripts}/git/list-code-files.sh | RunTypescriptCompiler "whole project" || die
else
  ${dir_scripts}/git/list-code-files.sh | RunEslint "whole project" || die
  ${dir_scripts}/git/list-code-files.sh | RunTypescriptCompiler "whole project" || die
fi
cd "$dir_scripts"
echo "> Validate done"
