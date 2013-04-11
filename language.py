import re
import string

EVENTS = ['Breakfast','Morning','Lunch','Afternoon','Dinner','Desert','Evening','Overnight']
NEGATION = ['not ','no ','without ','in','un']
PRIORITY = ['really','very','extremely','lot','lots','many','extraordinarily']
plan = {}
def get_keywords(query):
	query = string.lower(query)
	words = query.split(' ')
       	b_hi = regex_check(['expensive','fancy'],query)
	b_lo = regex_check(['cheap'],query)
	plan['budget'] = (b_hi - b_lo + 3) / 1.5
	
	k = regex_check(['kid','child','son','daughter'],query)
	if k == -1 or k == 0:
		plan['kids'] = False
	else:
		plan['kids'] = True
	location = re.search('(in|near) (\w+)',query)
	if location:
		plan['location'] = location.group(2)
	days = re.search('([0-9]+) day',query)
	if days:
		plan['events'] = ["Day "+str(i+1)+": "+e for i in range(int(days.group(1))) for e in EVENTS]
		plan['events'].pop(len(plan['events']) - 1)
	else:
		plan['events'] = [word.title() for word in query.split(' ') if word.title() in EVENTS]
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
