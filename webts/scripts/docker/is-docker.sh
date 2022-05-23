#!/bin/sh
set -e

IsDocker () {
  if which docker > /dev/null ; then
    return 0
  else
    return 1
  fi
}
