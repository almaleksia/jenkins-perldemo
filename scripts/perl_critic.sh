#!/bin/bash
SEVERITY_LEVEL=$1

is_valid=`echo $SEVERITY_LEVEL | ggrep -P '^\d$'`

if [[ "x" == "x$is_valid" ]]; then
	echo -e "Invalid arguments: Severity level argument must be a digit"
	exit 1
fi

find . | ggrep -v 'local' | grep -v 'app\.pl'| ggrep -P '(\.pl|\.psgi|\.pm)' | 
xargs /usr/local/bin/perlcritic -$SEVERITY_LEVEL