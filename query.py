#!/usr/bin/python

import sys

event = sys.argv[1]
# [first, second, third]
keywords = sys.argv[2]
days = int(sys.argv[3])
options = int(sys.argv[4])
distance = float(sys.argv[5])
lat = float(sys.argv[6])
lon = float(sys.argv[7])



keywords = keywords.rstrip(']')
keywords = keywords.lstrip('[')

cat = ""
for k in keywords.split(","):
       	cat += '\'' + k + '\', '
cat = cat[:-2]
a = []


i = -1

for e in event.split(","):
	e = e.lower()
	e = e.strip('][')
	if e=='breakfast' or e=='lunch' or  e=='dinner' or  e=='nightlife' or  e=='overnight':
		if e=='breakfast':
			i = i+1
		sql = ('select b.name, address, city, state, latitude, longitude, stars, photoUrl '
		       'from business as b, belongs as be '
		       'where id = b_id AND be.name in ('+str(cat)+') '
		       'AND  (select SQRT(POWER(latitude-'+str(lat)+',2) + POWER(longitude-'+str(lon)+',2)) '
		       'from businesses b1 where b.id = b1.id) <= '+str(distance) + ' '
		       'AND (\''+ str(e) +'\')  in (select be1.name '
		       'from belongs as be1 '
		       'where b.id = be1.businessId) '
		       'order by metric '
		       'limit ' + str(i) + ',1 ')
		a.append(sql)
	else: 
		sql = ('select b.name, address, city, state, latitude, longitude, stars, photoUrl '
		       'from business as b, belongs as be'
		       'where id = businessId AND belongs.name in ('+str(cat)+') '
		       'AND  (select SQRT(POWER(latitude-'+str(lat)+',2) + POWER(longitude-'+str(lon)+',2)) '
		       'from businesses as b1 where b.id = b1.id) <= '+str(distance) + ' '
		       'order by metric '
		       'limit ' + str(i) + ',1 ')
		a.append(sql)
print "~".join(a)
