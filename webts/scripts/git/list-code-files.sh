#!/bin/sh
set -e

{
  find . -type f;
} | grep -E '^(\.\/)?webts\/' | awk '!arr[$1]++'
