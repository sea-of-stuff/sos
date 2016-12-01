#! /usr/bin/env python
import csv
import datetime
import requests
import sys


def URL(row):
    return '%s:%s%s' % (row[1], row[2], row[3])

def GET(url):
    response = requests.request("GET", url)
    print(response.text)


experiment_definitions = sys.argv[1]
with open(experiment_definitions, 'rb') as f:
    reader = csv.reader(f)
    headers = reader.next()
    print 'CSV File: %s \nHeaders: %s \n' % (experiment_definitions, headers)
    for row in reader:
        if len(row) == 4:
            url = URL(row)

            a = datetime.datetime.now()
            GET(url)
            b = datetime.datetime.now()
            c = b - a
            print "Request time: %d ms" % (c.microseconds/1000)
