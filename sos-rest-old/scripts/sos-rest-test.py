import datetime
import json
import urllib2

url = "https://unsplash.it/1024/1024?image=%d"

for i in range(106, 120):
	a = datetime.datetime.now()
	bundle = [{
		"Type" : "cache",
		"Location" : (url % i)
	}]
	print bundle

	req = urllib2.Request('http://localhost:8080/sos/add/atom/locations')
	req.add_header('Content-Type', 'application/json')

	response = urllib2.urlopen(req, json.dumps(bundle))
	b = datetime.datetime.now()
	print (b - a)
