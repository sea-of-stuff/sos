#!/bin/bash
set -ex

echo 'Cleaning up all data'
rm -f data/* 

echo 'Cleaning up the db and other info'
rm -f node/*

echo 'Cleaning up the manifests'
rm -f manifests/*

echo 'Cleaning up the keys'
rm -f keys/*

echo 'Cleaning up the user/roles'
rm -f usro/*


