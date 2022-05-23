#!/bin/sh
set -e

{
  find . -type f;
} | awk '!arr[$1]++'
