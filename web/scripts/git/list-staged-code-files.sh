#!/bin/sh
set -e

{
  git diff --staged --name-only --diff-filter d;
} | grep '^web/' | awk '!arr[$1]++'
