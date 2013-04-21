#!/usr/bin/python



def get_events(keywords):
	events = [make_query(event,keywords) for event in keywords['events'])

def make_query(event,keywords,days,options,distance):
	l = days*options

	cat = ""
	for k in keywords:
		cat += k + ', '
	cat = cat[:-2]

	a = []

	for e in event:
		if t=='breakfast' | t=='lunch' | t=='dinner' | t=='nightlife' | t=='overnight':
			sql = ('select b.name, address, city, state, lat, lon, avg_stars, photo '
			    'from business as b, belongs as be '
			    'where id = b_id AND be.name in ('+cat+') '
			    'AND  (select SQRT(POWER(lat-'+lat+',2) - POWER(lon-'+lon+',2)) '
			    'from businesses b1 where b.id = b1.id) <= '+distance + ' '
			    'AND '+ e +'  in (select be1.name '
			    'from belongs as be1 '
			    'where be.b_id = be1.b_id) '
			    'order_by metric '
			    'limit 0, ' + l + ' ')
			a.append(sql)
		else: 
			sql = ('select b.name, address, city, state, lat, lon, avg_stars, photo '
			    'from business as b, belongs '
			    'where id = b_id AND belongs.name in ('+cat+') '
			    'AND  (select SQRT(POWER(lat-'+lat+',2) - POWER(lon-'+lon+',2)) '
			    'from businesses b1 where b.id = b1.id) <= '+distance + ' '
			    'order_by metric '
			    'limit 0, ' + l + ' ')
			a.append(sql)
		return "~".join(a)

