#!/bin/sh
set -e
cd "${0%/*}"
. ./_variables.sh

ValidateAll () {
  cd "$dir_root"
  files="$(find . -type f | awk '!arr[$1]++' | grep "$packages_regex" | grep -v "node_modules" | grep "$eslint_files_regex" | xargs echo)"
  if [ "$files" ]; then
    echo "> Validating with eslint (whole project)"
    npx yarn eslint --format unix --ignore-pattern 'node_modules/*' $files
  else
    echo "> Validating with eslint (whole project): no changed files found"
  fi
  cd "$dir_scripts"

#  cd "$dir_root"
#  files="$(find . -type f | awk '!arr[$1]++' | grep "$packages_regex" | grep -v "node_modules" | grep "$prettier_files_regex" | xargs echo)"
#  if [ "$files" ]; then
#    echo "> Validating with prettier (whole project)"
#    npx yarn prettier --no-error-on-unmatched-pattern --check $files
#  else
#    echo "> Validating with prettier (whole project): no changed files found"
#  fi
#  cd "$dir_scripts"

  cd "$dir_root"
  files="$(find . -type f | awk '!arr[$1]++' | grep "$packages_regex" | grep -v "node_modules" | xargs echo)"
  if [ "$files" ]; then
    echo "> Validating with tsc (whole project)"
    npx tsc --noEmit
  else
    echo "> Validating with tsc (whole project): no changed files found"
  fi
  cd "$dir_scripts"
}

ValidateAllCiGitlab () {
  cd "$dir_root"
  if [ ! -d "./build" ]; then mkdir "build"; fi
  cd "$dir_scripts"

  cd "$dir_root"
  files="$(find . -type f | awk '!arr[$1]++' | grep "$packages_regex" | grep -v "node_modules" | grep "$eslint_files_regex" | xargs echo)"
  if [ "$files" ]; then
    echo "> Validating with eslint (whole project)"
    ESLINT_FORMATTER=html \
    ESLINT_CODE_QUALITY_REPORT=./build/code-quality-report-gitlab.json \
    npx eslint --format ./scripts/eslint/eslint-formatter-gitlab.js $files > ./build/code-quality-report.html
  else
    echo "> Validating with eslint (whole project): no changed files found"
  fi
  cd "$dir_scripts"

#  cd "$dir_root"
#  files="$(find . -type f | awk '!arr[$1]++' | grep "$packages_regex" | grep -v "node_modules" | grep "$prettier_files_regex" | xargs echo)"
#  if [ "$files" ]; then
#    echo "> Validating with prettier (whole project)"
#    npx yarn prettier --no-error-on-unmatched-pattern --check $files
#  else
#    echo "> Validating with prettier (whole project): no changed files found"
#  fi
#  cd "$dir_scripts"

  cd "$dir_root"
  files="$(find . -type f | awk '!arr[$1]++' | grep "$packages_regex" | grep -v "node_modules" | xargs echo)"
  if [ "$files" ]; then
    echo "> Validating with tsc (whole project)"
    npx tsc --noEmit
  else
    echo "> Validating with tsc (whole project): no changed files found"
  fi
  cd "$dir_scripts"
}

ValidateOnlyChanged () {
  cd "$dir_root"
  files="$(${dir_scripts}/git/list-changed-code-files.sh | grep "$packages_regex" | grep -v "node_modules" | grep "$eslint_files_regex" | xargs echo)"
  if [ "$files" ]; then
    echo "> Validating with eslint (only changed files)"
    npx yarn eslint --format unix --ignore-pattern 'node_modules/*' $files
  else
    echo "> Validating with eslint (only changed files): no changed files found"
  fi
  cd "$dir_scripts"

#  cd "$dir_root"
#  files="$(${dir_scripts}/git/list-changed-code-files.sh | grep "$packages_regex" | grep -v "node_modules" | grep "$prettier_files_regex" | xargs echo)"
#  if [ "$files" ]; then
#    echo "> Validating with prettier (only changed files)"
#    npx yarn prettier --no-error-on-unmatched-pattern --check $files
#  else
#    echo "> Validating with prettier (only changed files): no changed files found"
#  fi
#  cd "$dir_scripts"

  cd "$dir_root"
  files="$(${dir_scripts}/git/list-changed-code-files.sh | grep "$packages_regex" | grep -v "node_modules" | xargs echo)"
  if [ "$files" ]; then
    echo "> Validating with tsc (whole project)"
    npx tsc --noEmit
  else
    echo "> Validating with tsc (whole project): no changed files found"
  fi
  cd "$dir_scripts"
}

ValidateOnlyStaged () {
  cd "$dir_root"
  files="$(${dir_scripts}/git/list-staged-code-files.sh | grep "$packages_regex" | grep -v "node_modules" | grep "$eslint_files_regex" | xargs echo)"
  if [ "$files" ]; then
    echo "> Validating with eslint (only staged files)"
    npx yarn eslint --format unix --ignore-pattern 'node_modules/*' $files
  else
    echo "> Validating with eslint (only staged files): no staged files found"
  fi
  cd "$dir_scripts"

#  cd "$dir_root"
#  files="$(${dir_scripts}/git/list-staged-code-files.sh | grep "$packages_regex" | grep -v "node_modules" | grep "$prettier_files_regex" | xargs echo)"
#  if [ "$files" ]; then
#    echo "> Validating with prettier (only staged files)"
#    npx yarn prettier --no-error-on-unmatched-pattern --check $files
#  else
#    echo "> Validating with prettier (only staged files): no staged files found"
#  fi
#  cd "$dir_scripts"

  cd "$dir_root"
  files="$(${dir_scripts}/git/list-staged-code-files.sh | grep "$packages_regex" | grep -v "node_modules" | xargs echo)"
  if [ "$files" ]; then
    echo "> Validating with tsc (whole project)"
    npx tsc --noEmit
  else
    echo "> Validating with tsc (whole project): no staged files found"
  fi
  cd "$dir_scripts"
}

echo "> Validate"
if [ "$1" = "staged" ]; then
  ValidateOnlyStaged
elif [ "$1" = "changed" ]; then
  ValidateOnlyChanged
elif [ "$1" = "ci" ]; then
  ValidateAllCiGitlab
else
  ValidateAll
fi
echo "> Validate done"
