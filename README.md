Instaplan
=========
We made plans before it was cool


CIS 330 Final Project - Su(san, darshan, ung), and Noam

Folders:
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
  
  
