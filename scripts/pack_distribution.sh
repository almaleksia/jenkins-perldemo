#!/bin/bash
PROJECT_NAME=$1
BUILD_ID=$2
TARDIR=/tmp/$PROJECT_NAME-tardir
TARREPO=$HOME/tarballs

carton install --deployment
rm -rf $TARDIR
mkdir -p $TARDIR

cp -r lib $TARDIR
cp -r local $TARDIR 
cp app.pl $TARDIR
cd $TARDIR


mkdir -p $TARREPO
tar -cvf $TARREPO/${PROJECT_NAME}-${BUILD_ID}.tar .

