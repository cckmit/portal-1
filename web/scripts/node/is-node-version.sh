#!/bin/sh
set -e

IsNodeVersion () {
    expected=$1
    actual=$(./node/node-version-major.sh)
    if [ "$expected" = "$actual" ]; then
        return 0
    else
        return 1
    fi
}
