#!/bin/sh
set -e

{
  git diff --staged --name-only --diff-filter d;
} | grep -E '^(\.\/)?webts\/' | awk '!arr[$1]++'
