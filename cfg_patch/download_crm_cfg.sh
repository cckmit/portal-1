#!/bin/bash

export PATH=/bin:/usr/bin:/sbin:/usr/sbin:/usr/local/mysql/bin
export LC_ALL=C

echo "** download cfg ** "
mkdir -p crm
ssh -tt -L 1234:10.0.0.18:22 support@192.168.110.68 &
sleep 1.0
scp -P 1234 frost@localhost:/tomcat/cfg/portal.properties ./crm/portal.properties
pkill -f 'ssh -tt -L 1234:10.0.0.18:22'
cp ./crm/portal.properties ./crm/portal_edit.properties
echo "** download cfg end ** "

