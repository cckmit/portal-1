#!/bin/sh
set -e

IsNodeVersion () {
    if which node > /dev/null ; then
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
