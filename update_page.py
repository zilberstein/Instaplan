#!/usr/bin/python
import sys
import language

def create_event(activity,event):
	return '<div class="activity"><h2>'+activity+'</h2><h3>'+event+'</h3><p><img src="example.jpg" width="400px" />Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus ipsum tortor, lacinia eu dictum nec, ullamcorper ac enim. Donec sed mi non tortor malesuada molestie ut ac velit. Cras neque ipsum, semper nec iaculis nec, adipiscing ut diam. Sed sit amet pellentesque massa. Vestibulum placerat posuere iaculis. Ut consequat tempus arcu, id porta purus varius eu. Sed velit tellus, facilisis sit amet tincidunt ut, feugiat ut diam. Nam sodales congue enim, vel laoreet ante egestas ut. Nam eget augue dolor. Pellentesque ut ante magna. Nulla hendrerit nulla eu dolor varius quis volutpat leo malesuada. Sed eleifend dictum molestie. Nunc elit risus, pulvinar non congue sit amet, dapibus sit amet massa. Etiam tortor est, fermentum sit amet porta id, imperdiet sit amet arcu. Cras ac nisl erat. Aenean sodales tortor ac augue pulvinar eu commodo turpis ornare.</p></div>'


keywords = {'location': sys.argv[1], 'days': int(sys.argv[2]), 'distance': float(sys.argv[3]), 'catagories':sys.argv[4], 'options':sys.argv[5]}
keywords['events'] = language.make_events(keywords['days'])
language.create_sidebar(keywords)
print keywords,

print ''.join([create_event("Activity",event) for event in keywords['events']])
