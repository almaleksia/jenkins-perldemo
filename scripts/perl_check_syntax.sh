#!/bin/bash

sudo carton install --deployment
export PERL5LIB=./local/lib/perl5

find . | ggrep -P '(\.t|\.pl|\.pm|.psgi)$' | xargs perl -c -Ilib