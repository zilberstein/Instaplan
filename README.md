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
  index.php: index page for the site
  language.py:  
    our NLP, associates common words with given categories and events and looks for location and duration 
    of stay to figure out what the user is looking for
  login.php/logout.php:
    Connects to the database to check if the username and password are valid. 
  
  
