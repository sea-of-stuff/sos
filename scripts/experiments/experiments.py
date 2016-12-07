#! /usr/bin/env python
import ast
import csv
import datetime
import requests
import sys

print '******************\nSTARTING EXPERIMENTS SCRIPT\n\n'

def URL(row):
    return '%s:%s%s' % (row[1], row[2], row[3])

def printResponse(response):
    print ">> Response: %s" % (response.status_code)

def GET(url):
    response = requests.request("GET", url)
    printResponse(response)

def POST(url, headers={}, files={}, json={}):
    if bool(files):
        files = {'file': open(files, 'rb')}
    response = requests.request("POST", url, headers=headers, files=files, json=json)
    printResponse(response)


experiment_definitions = sys.argv[1]
with open(experiment_definitions, 'rb') as f:
    reader = csv.reader(f)
    headers = reader.next()
    print 'CSV File: %s \nHeaders: %s \n' % (experiment_definitions, headers)
    for row in reader:
        if len(row) >= 4:
            print "Request: %s" % row

            url = URL(row)
            a = datetime.datetime.now()

            if row[0] in 'GET':
                GET(url)
            elif row[0] in 'POST':
                headers = ast.literal_eval(row[4]) # convert string to dict
                payload = row[5]

                if headers['content-type'] in 'application/json':
                    payload = ast.literal_eval(payload) # convert string to dict
                    POST(url, headers, json=payload)
                elif headers['content-type'] in 'multipart/form-data':
                    POST(url, headers, files=payload)

            else:
                print 'Method unknown'

            b = datetime.datetime.now()
            c = b - a
            print "Request time: %d ms\n" % (c.total_seconds() * 1000)

print '\n\nEXPERIMENTS SCRIPT FINISHED\n******************'
