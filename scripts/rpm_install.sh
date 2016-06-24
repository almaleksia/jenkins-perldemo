#!/bin/bash

PROJECT_NAME=$1
BUILD_ID=$2

sudo rpm -i --nodeps $HOME/rpm-repo/${PROJECT_NAME}-0.${BUILD_ID}-1.noarch.rpm