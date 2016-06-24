#!/bin/bash

SCRIPT='starman/restart.sh'
SCRIPTPATH=`dirname $SCRIPT`
pid_file=/tmp/starman.pid

if [ -f $pid_file ]; then
        pid=`cat $pid_file`;

        if [ "x$pid" != "x"  ]; then
                kill $pid;
        fi
fi

cd /var/www/perldemo
mkdir -p starman
starman --workers 1 --port 5000 --daemonize \
--error-log ${SCRIPTPATH}/starman_error_log \
--acess-log ${SCRIPTPATH}/starman_access_log \
--pid $pid_file ${SCRIPTPATH}/../app.pl

