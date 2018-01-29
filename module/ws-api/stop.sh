#!/usr/bin/env bash

APIDIR=/home/turik/workspace/portal4/module/ws-api

PFILE=$APIDIR/var/pidfile

if [ -f "$PFILE" ]
then

 PID=$(< $PFILE)

 if [ -d '/proc/'$PID ]
 then
   echo "send kill to $PID" ;
   kill -15 $PID ;
   printf 'wait until it stops ';

   while [ -d '/proc/'$PID ]; do
     printf ".";
     sleep 0.5
   done

   echo 'stopped';
 else
   echo "process with id $PID not found";
 fi

 rm -f $PFILE

else
  echo "is not running, nothing to stop";
fi