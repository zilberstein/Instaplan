Instaplan
=========
We made plans before it was cool


CIS 330 Final Project - Su(san, darshan, ung), and Noam

Folders:
  Build: 
    n/a
  Images:
    Contains all images used
  Instaplan:
    Similar to HW4, contains classes for each of the main tables and intermediate types with a 
    method to create a new object for the ease of converting JSON into an intermediate object and then
    in to an object with all the fields we need for the database
    Also contains a main method which parses the JSON into an intermediate type that has attributes for
    all of the entries in the JSON file. That object is then converted into another object that has the information
    that is needed to be inserted into the databse. In order to convert the Yelp categories to the 
    categories we defined there is a method that goes through all of the text files (name=our category, 
    contents=list of associated Yelp categories) and creates a hashmap from Yelp category to a set of
    our categories. The connection with the database is made and the tables are created and inserted in to
    using the objects created earlier in the main.
  Instaplan2:
    Depricated version of the above folder, created by merge issues
  mysql-connector:
    Imported opensource JDBC Driver for MySQL. See internal README for more information 
  python-sql:
    Tried to use python to query databse, unfortunately it required us to download something on to ENIAC, which 
    we were unable to do. Depricated

Other Files:
  generate_page.py:
    Creates all of the content for the results page 
  index.php: 
    index page for the site
  language.py:  
    our NLP, associates common words with given categories and events and looks for location and duration 
    of stay to figure out what the user is looking for
  login.php/logout.php:
    Connects to the database to check if the username and password are valid, does not allow login if either
    username or password is invalid. Sets session username to null, logging the user out
  page.html:
    Page that is shown after a user has typed in a query. Sidebar with options to fine tune parameters like number of options.
    Displays suggestions for each timeslop a user has asked for
  query.py:
    Python script that creates a query for every given time slot based on what the user has requested. Some of the timeslots,
    such as breakfast and dinner are more restrictive based on the nature of the activity, hence the need for two different
    queries. Outputs a single string to the console containing all of the queries delimited by a "~"
  register.php:
    Allows a user to create account. It restricts user input and will not let a user sign up if their input is not
    valid. It also makes sure that the username they are trying to use has already been taken. It then puts all of 
    the user's info into the database and automatically logs them in and has a greeting using their first and last
    name below the query box.
  result.php:
    Displays a map showing where all of the businesses are using the GoogleMapsAPI. It uses generate_page.py to create
    all of the content for the page
  update_page.py:
    If a user changes any of the options on the side bar it reloads the page with the neww results.
  
  
