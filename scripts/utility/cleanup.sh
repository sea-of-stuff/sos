#!/bin/bash
set -ex

echo 'Cleaning up all data'
rm -f data/* 

echo 'Cleaning up the db'
rm -f node/*

echo 'Cleaning up the manifests'
rm -f manifests/* 


