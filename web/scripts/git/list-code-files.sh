#!/bin/sh
set -e

{
  find files .;
} | awk '!arr[$1]++' | grep ''
