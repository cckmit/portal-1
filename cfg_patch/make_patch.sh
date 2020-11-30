#!/bin/bash

export PATH=/bin:/usr/bin:/sbin:/usr/sbin:/usr/local/mysql/bin
export LC_ALL=C

echo "** make patch cfg ** "
cd ./cfg
diff -Naur portal.properties portal_edit.properties > ../result/cfg-patch.txt



