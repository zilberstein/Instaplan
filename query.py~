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


for e in event.split(","):
	e = e.lower()
	e = e.strip('][')
	if cat == "\'\'":
		catSearch = ""
	else:
		catSearch = "AND be.name in (" + str(cat) + ") "
	
	if e=='breakfast' or e=='lunch' or  e=='dinner' or  e=='nightlife' or  e=='overnight' or e=='desert':
		sql = ('select b.name, address, city, state, latitude, longitude, stars, photoUrl, text, id '
		       'from business as b, belongs as be, review as r '
		       'where id = be.businessId '  
		       'AND  (select SQRT(POWER(latitude-'+str(lat)+',2)*69 + POWER(longitude-'+str(lon)+',2)*53) '
		       'from business b1 where b.id = b1.id) <= '+str(distance) + ' '
		       'AND (\''+ str(e) +'\',\'restaurant\')  in (select be1.name '
		       'from belongs as be1 '
		       'where b.id = be1.businessId) '
		       'AND r.businessId = b.id '
		       + catsearch + 
		       'order by metric desc '
		       'limit 0, 1 ')
		a.append(sql)
	else: 
		sql = ('select b.name, address, city, state, latitude, longitude, stars, photoUrl, text, id '
		       'from business as b, belongs as be, review as r '
		       'where id = be.businessId ' + catSearch + 
		       'AND  (select SQRT(POWER(latitude-'+str(lat)+',2)*69 + POWER(longitude-'+str(lon)+',2)*53) '
		       'from business as b1 where b.id = b1.id) <= '+str(distance) + ' '
		       'AND r.businessId = b.id '
		       + catsearch + 
		       'order by metric desc '
		       'limit 0,1 ')
		a.append(sql)
print "~".join(a)
