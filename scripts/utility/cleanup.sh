#!/bin/bash
set -ex

echo 'Cleaning up the cache'
rm -f caches/* 

echo 'Cleaning up all data'
rm -f data/* 

echo 'Cleaning up the db'
rm -f db/* 

echo 'Cleaning up the heads'
rm -f heads/* 

echo 'Cleaning up the manifests'
rm -f manifests/* 

echo 'Cleaning up the metadata'
rm -f metadata/*
