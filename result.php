<?
//error_reporting(0);
session_start();

$db=mysqli_connect("SQL09.FREEMYSQL.NET", "instaplan", "cis330");
/* check connection */
if (mysqli_connect_errno()) {
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
}
mysqli_select_db($db, "instaplan");

$events = "\"[".$_POST['events']."]\"";
$keywords = "\"[".$_POST['categories']."]\"";
$days= "\"".$_POST['days']."\"";
$options = "\"".$_POST['options']."\"";
$distance = "\"".$_POST['distance']."\"";
$location = "\"".$_POST['location']."\"";

$add = str_replace(" ", "+", $location);
$add = str_replace(",", "+", $add);
$url="http://maps.googleapis.com/maps/api/geocode/json?address=$add&sensor=false";
$source = file_get_contents($url);
$obj = json_decode($source);
$lat = "\"".$obj->results[0]->geometry->location->lat."\"";
$long = "\"".$obj->results[0]->geometry->location->lng."\"";
?>

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
	<?
	$command = "python query.py ".$events." ".$keywords." ".$days." ".$options." ".$distance." ".$lat." ".$long;
	$sql= exec($command);
	$commands= explode("~",$sql);
	$events= explode(",",$events);
	
	$output= array();
	for($i=0;$i<count($commands);$i++)
	{
		$result = mysqli_query($db,$commands[$i]);
		if(mysqli_num_rows($result) == 1)
		{
			$row = mysqli_fetch_row($result);
			$row[8] = $events[$i];
			$output[]=$row;
		}
	}
	
	print_r ($output);
	
	/*
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
*/
	   ?>

      </div>
    </div>
  </body>
</html>
