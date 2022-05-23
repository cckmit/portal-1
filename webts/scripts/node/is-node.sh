#!/bin/sh
set -e

IsNode () {
  if which node > /dev/null ; then
    return 0
  else
    return 1
  fi
}

IsNodeVersion () {
  if IsNode ; then
    expected=$1
    actual=$(./node/node-version-major.sh)
    if [ "$expected" = "$actual" ]; then
      return 0
    else
      return 1
    fi
  else
    return 1
  fi
}
