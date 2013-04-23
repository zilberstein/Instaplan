#!/usr/bin/python

import re
import string
import json
import sys

CATEGORIES = ['active', 'breakfast', 'college', 'culture', 'dessert', 'dinner', 'family', 'kids', 'lunch', 'nightlife', 'old_people']
EVENTS   = ['Breakfast','Morning','Lunch','Afternoon','Dinner','Dessert','Evening','Overnight']
EVENTMAP = {'morning':['Breakfast','Morning'], 'afternoon':['Lunch','Afternoon'],'evening':['Dinner','Nightlife'],'night':['Afternoon','Dinner','Evening','Overnight','Breakfast','Morning','Lunch'],'day':EVENTS,'breakfast':['Breakfast'], 'lunch':['Lunch'], 'dinner':['Dinner'], 'overnight':['Dinner','Evening','Overnight','Breakfast']}
NEGATION = ['not ','no ','without ','in','un','non','high ', '0 ', 'hate ']
PRIORITY = ['really ','very ','extremely ','lot ','lots ','many ','extraordinarily ','low ','highly ']
plan = {}

SINGLE_DIGITS = {'zero':0,'one':1,'two':2,'three':3,'four':4,'five':5,'six':6,'seven':7,'eight':8,'nine':9,'ten':10,'eleven':11,'twelve':12,'thirteen':13,'fourteen':14,'fifteen':15,'sixteen':16,'seventeen':17,'eighteen':18,'nineteen':19}
TENS = {'twenty':2,'thirty':3,'fourty':4,'fifty':5,'sixty':6,'seventy':7,'eighty':8,'ninety':9}

def get_keywords(query):
	query = reformat(query)
	words = query.split(' ')
       	b_hi = regex_check(['far'],query)
	b_lo = regex_check(['near','close'],query)
	plan['distance'] = (b_hi - b_lo + 3) / 1.5
	
	plan['options'] = 1
	
	f = open('categories/cities.txt','r')
	cities = [city.strip() for city in f]
	f.close()
	plan['location'] = regex_get(cities, query).title()

	days = re.search('([0-9]+) day',query)
	if days and days.group(1) != '0':
		plan['days'] = int(days.group(1))
		plan['events'] = make_events(int(days.group(1)))
	else:
		plan['events'] = []
		for word in query.split(' '):
			if word in EVENTMAP:
				plan['events'] += EVENTMAP[word]
		if plan['events'] == []:
			plan['events'] = EVENTS
		plan['days'] = 1

	
	# Find keywords
	categories = make_cats()
	plan['categories'] = list(set([cat_name for cat_name in categories if regex_check(categories[cat_name],query) in [1,2]]))

	return plan


def regex_check(word,query):
	checker = re.search('(\w+ ?)(%(word)s)' % {'word':'|'.join(word)},query)
	if checker is not None:
		if checker.group(1) in NEGATION:
			return -1
		elif checker.group(1) in PRIORITY:
			return 2
		else:
			return 1
	else:
		return 0

def regex_get(word,query):
	checker = re.search('(\w+ ?)(%(word)s)' % {'word':'|'.join(word)},query)
	if checker is not None:
		if checker.group(1) in NEGATION:
			return None
		else:
			return checker.group(2)
	else:
		return None


def reformat(s):
	s = "X "+s;
	s = s.lower()
	s = re.sub('[^\w ]+', '', s)
	single_dig = '|'.join(SINGLE_DIGITS)
	reg = re.findall('(\w+)(.?)('+single_dig+') ',s)
	if reg is not None:
		for r in reg:
			if r[0] in TENS:
				num = ''
				num += str(TENS[r[0]])
				num += str(SINGLE_DIGITS[r[2]])
				s = s.replace(r[1].join([r[0],r[2]]),num)
			else:
				num = str(SINGLE_DIGITS[r[2]])
				s = s.replace(r[2],num)
	reg = re.findall('|'.join(TENS),s)
	if reg is not None:
		for r in reg:
			s = s.replace(r,str(TENS[r])+'0')
	return s

def make_events(days):
    if days > 0:
        events = [e for i in range(days) for e in EVENTS]
        events.pop(len(events) - 1)
        return events
    else:
        return []

def make_cats():
	categories = {}
	for c in CATEGORIES:
		f = open('categories/'+c+'.txt', 'r')
		categories[c] = [word.strip().replace('_',' ') for word in f]
	return categories

print json.dumps(get_keywords(sys.argv[1]))
