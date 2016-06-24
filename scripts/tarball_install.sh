#!/bin/bash
PROJECT_ID=$1
BUILD_ID=$2
DEST_DIR=/var/www/$PROJECT_ID

rm -rf $DEST_DIR
mkdir $DEST_DIR
tar -xvf $HOME/tarballs/${PROJECT_ID}-${BUILD_ID}.tar -C $DEST_DIR