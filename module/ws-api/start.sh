#!/usr/bin/env bash

APIDIR=/home/turik/workspace/portal4/module/ws-api

cd ./target
java -jar portal-api.jar 1>> $APIDIR/logs/api.out.log 2>>$APIDIR/logs/api.err.log &

echo $! > $APIDIR/var/pidfile
echo "Portal API run, pid is $!"