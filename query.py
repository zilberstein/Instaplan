#!/usr/bin/python

def make_query(event,keywords):
	return 'SELECT...'

def get_events(keywords):
	events = [make_query(event,keywords) for event in keywords['events'])
	
