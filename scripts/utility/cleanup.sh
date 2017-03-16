#!/bin/bash
set -ex

echo 'Cleaning up the cache'
rm -f caches/* 

echo 'Cleaning up all data'
rm -f data/* 

echo 'Cleaning up the db'
rm -f db/* 

echo 'Cleaning up the manifests'
rm -f manifests/* 


