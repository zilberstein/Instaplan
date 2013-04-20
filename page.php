<!DOCTYPE HTML>
<html>
  <head>
    
    <title>Instaplan</title>

    <link rel="stylesheet" type="text/css" href="style.css" />
  </head>
  <body>
    <div id="bar">
      <a href="index.php"><img src='instaplan-mini.png' width=150px /></a>
    </div>
    <div id="container2">
      <div id="adjust">
	<h3>Fine Tuner</h3>
	<form action="result.php" method="get">
	  <input type="hidden" name="type" value="adjust">
	  <h5>Budget</h5>
	  $ <input class="slide" type="range" min="0" max="20" width="100px" /> $$$$
	  <h5>Distance</h5>
      <input class="slide" type="range" min="0" max="20" width="100px" />
      <h5>Transportation</h5>
      <select>
	<option>Public Transport</option>
	<option>Car</option>
	<option>Walking</option>
	<option>Fixie</option>
      </select><br /><br />
      <input type="submit" />
	</form>
      </div>
      <div id="content">
	
	<h1>My Plan</h1>
	
	<?php
	   $arg = $_GET['plan'];
	   echo "<h2>&ldquo;$arg&rdquo;</h2>";
	   $command = "python generate_page.py \"$arg\"";
	   echo $command;
	   echo exec($command)
	   ?>
	
      </div>
    </div>
  </body>
</html>
