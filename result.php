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
    <a href="index.php"><img src='images/instaplan-mini.png' width=150px /></a>
    </div>
    <div id="container2">
	<?php
	   if ($_POST['type'] == 'language') {
	   $arg = $_POST['plan'];
	   $command = "python generate_page.py \"$arg\"";
	   } else {
        $days = $_POST['days'];
        $loc = $_POST['location'];
	$distance = $_POST['distance'];
	$catagories = $_POST['catagories'];
        $command = "python update_page.py $loc $days $distance $catagories";
	 echo "$command";
	   }
        echo exec($command);

	   ?>

      </div>
    </div>
  </body>
</html>
