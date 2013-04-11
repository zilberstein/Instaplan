<!DOCTYPE HTML>
<html>
  <head>
    
    <title>Instaplan</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" type="text/css" href="style.css" />
    <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCBK8GNOmG7wnFlSAzX9Udzrn1oFnUokLs&sensor=false">
    </script>
    <script type="text/javascript">
     function initialize() {
        var myLatlng = new google.maps.LatLng(39.9522,-75.1642);
        var mapOptions = {
          zoom: 15,
          center: myLatlng,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        }
        var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

        var marker = new google.maps.Marker({
            position: myLatlng,
            map: map,
            title: 'Hello World!'
        });
      }
    </script>
  </head>
  <body onload="initialize()">
    <div id="bar">
      <a href="index.php"><img src='instaplan-mini.png' width=150px /></a>
    </div>
    <div id="container2">
      <div id="adjust">
	<h3>Fine Tuner</h3>
	<form action="result.php" method="get">
	  <input type="hidden" name="type" value="adjust">
	  <h5>Budget</h5>
	  $ <input class="slide" name="budget" type="range" min="0" max="4" width="100px" /> $$$$
	  <h5>Distance</h5>
      <input class="slide" name="dist" type="range" min="0" max="20" width="100px" />
      <h5>Transportation</h5>
      <select name="transport">
	<option>Public Transport</option>
	<option>Car</option>
	<option>Walking</option>
	<option>Fixie</option>
      </select><br /><br />
      <input type="submit" />
	</form>
      </div>
      <div id="content">
	<div id="map-canvas"></div>
	<h1>My Plan</h1>

	<?php
	   if ($_GET['type'] == 'language') {
	   $arg = $_GET['plan'];
	   echo "<h2>&ldquo;$arg&rdquo;</h2>";
	   $command = "python generate_page.py \"$arg\"";
	   echo exec($command);
	   } else {
	   echo "<h2>You have no plans :(</h2>";
	   }
	   ?>
	
      </div>
    </div>
  </body>
</html>
