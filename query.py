#!/usr/bin/python

import sys

event = sys.argv[1]
keywords = sys.argv[2]
days = int(sys.argv[3])
options = int(sys.argv[4])
distance = float(sys.argv[5])
lat = float(sys.argv[6])
lon = float(sys.argv[7])

l = days*options

cat = ""
for k in keywords:
       	cat += k + ', '
cat = cat[:-2]
a = []

for e in event:
	e = e.lower()
	if e=='breakfast' or e=='lunch' or  e=='dinner' or  e=='nightlife' or  e=='overnight':
		sql = ('select b.name, address, city, state, lat, lon, avg_stars, photo '
		       'from business as b, belongs as be '
		       'where id = b_id AND be.name in ('+str(cat)+') '
		       'AND  (select SQRT(POWER(lat-'+str(lat)+',2) - POWER(lon-'+str(lon)+',2)) '
		       'from businesses b1 where b.id = b1.id) <= '+str(distance) + ' '
		       'AND '+ str(e) +'  in (select be1.name '
		       'from belongs as be1 '
		       'where be.b_id = be1.b_id) '
		       'order_by metric '
		       'limit 0, ' + str(l) + ' ')
		a.append(sql)
	else: 
		sql = ('select b.name, address, city, state, lat, lon, avg_stars, photo '
		       'from business as b, belongs '
		       'where id = b_id AND belongs.name in ('+str(cat)+') '
		       'AND  (select SQRT(POWER(lat-'+str(lat)+',2) - POWER(lon-'+str(lon)+',2)) '
		       'from businesses b1 where b.id = b1.id) <= '+str(distance) + ' '
		       'order_by metric '
		       'limit 0, ' + str(l) + ' ')
>>>>>>> 50f0711d967cdeaaed35d3042ed11d9f349e5f87
		a.append(sql)
print "~".join(a)
