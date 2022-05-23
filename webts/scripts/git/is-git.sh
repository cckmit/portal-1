#!/bin/sh
set -e

IsGit () {
  if which git > /dev/null ; then
    return 0
  else
    return 1
  fi
}
