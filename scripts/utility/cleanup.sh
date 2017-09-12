#!/bin/bash
set -ex

echo 'Cleaning up all data'
rm -f data/* 

echo 'Cleaning up the db and other info'
find node/ -type f ! -name 'id_rsa.*' -delete
#rm -f node/*

echo 'Cleaning up the manifests'
rm -f manifests/*

echo 'Cleaning up the keys'
rm -f keys/*

echo 'Cleaning up the user/roles'
rm -f usro/*

echo 'Cleaning up the keys'
rm -f keys/*

echo 'Cleaning up the contexts'
rm -rf context/*

echo 'Cleaning up the java classes'
rm -rf java/*
