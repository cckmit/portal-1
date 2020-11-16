#!/bin/bash

export PATH=/bin:/usr/bin:/sbin:/usr/sbin:/usr/local/mysql/bin
export LC_ALL=C

echo "** download cfg ** "
mkdir -p cfg
scp support@192.168.110.68:/opt/tomcat/latest/cfg/portal.properties ./cfg/portal.properties
cp ./cfg/portal.properties ./cfg/portal_edit.properties
echo "** download cfg end ** "



