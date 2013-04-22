<!DOCTYPE HTML>
<html>
<?
error_reporting(0);
session_start();

//if not arrived through index, go back to index
if($_POST['events']===null)
	header( 'Location: index.php');

$db=mysqli_connect("db4free.net", "instaplan", "cis330");
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

$command = "python query.py ".$events." ".$keywords." ".$days." ".$options." ".$distance." ".$lat." ".$long;
$sql= exec($command);

$commands= explode("~",$sql);
$events= explode(",",$_POST['events']);
 
$output= array();
$seen="''";
echo "<!--"."
";
for($i=0;$i<count($commands);$i++) 
{
	$query= $commands[$i];
	$pos= strpos($query,"order by");
	$query= substr($query,0,$pos)." AND be.businessId NOT IN ($seen) ".substr($query,$pos);
	echo $query."
	";
    $result = mysqli_query($db,$query);
    if(mysqli_num_rows($result) == 1) 
	{
      $row = mysqli_fetch_row($result);
	  $seen.=",'$row[9]'";
      $row[count($row)] = $events[$i];
      $output[]=$row;
    }
	else
	{
	  $cat="AND be.name in (";
	  $pos= strpos($query,$cat);
	  $end= strpos($query,")",$pos);
	  
	  $query= substr($query,0,$pos).substr($query,$end+1);
	  
	  $result = mysqli_query($db,$query);
	  if ($pos>0 && $end>$pos)
		echo "No results found, stripping category: ".$query."
	    ";
	  if(mysqli_num_rows($result) == 1) 
	  {
        $row = mysqli_fetch_row($result);
	    $seen.=",'$row[9]'";
        $row[count($row)] = $events[$i];
        $output[]=$row;
      }
	}
}
echo "
-->";
?>

    <title>Instaplan</title>
	<link rel="icon" href="images/favicon.ico">
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" type="text/css" href="style.css" />
    <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCBK8GNOmG7wnFlSAzX9Udzrn1oFnUokLs&sensor=false">
    </script> 
       <script type="text/javascript">
	 function initialize() {
	 var center_ll = new google.maps.LatLng(<?php 
			   $c_lat = 0;
			   $c_lng = 0;
			   for ($i=0; $i<count($output); $i++) {
				$c_lat = $c_lat + $output[$i][4];
				$c_lng = $c_lng + $output[$i][5];
			   }
			   $c_lat = $c_lat/count($output);
			   $c_lng = $c_lng/count($output);
			   echo "$c_lat,$c_lng";?>);
	 
         var mapOptions = {
         zoom: 15,
         center: center_ll,
         mapTypeId: google.maps.MapTypeId.ROADMAP
         }
         var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
	 var coordinates = new Array();
	 var markers = new Array();
	 <?php
	    for ($i=0; $i<count($output); $i++) {
	      $lat = $output[$i][4];
	      $long = $output[$i][5];
	      echo "coordinates[$i] = new google.maps.LatLng($lat,$long);";
	      echo "markers[$i] = new google.maps.Marker({";
	      echo "position: coordinates[$i],";
              echo "map: map,";
              echo "title: '".$output[$i][0]."'";
              echo "});";
	}
	?>
      }
    </script>
  </head>
  <body onload="initialize()">
    <div id="bar">
      <a href="index.php"><img src='images/instaplan-mini.png' width=150px /></a>
    </div>
    <div id="main">
      <div id="adjust"><h3>Fine Tuner</h3>
	<form action="result.php" method="post">
	  <input type="hidden" name="type" value="adjust">
	  <input type="hidden" name="categories" value="<?php echo $_POST['categories'];?>"/>
	  <h5>Location</h5>
	  <input type="text" name="location" value="<?php echo $_POST['location'];?>" />
	  <h5>Max Distance</h5>
	  <input type="hidden" name="type" value="update" />
	  <input class="slide" name="distance" type="range" min="0" max="4" step="0.2" value="<?php echo $_POST['distance'];?>" width="100px">
	  <h5>Duration</h5>
	  <input name="days" type="number" min="1" max="20" value="<?php echo $_POST['days'];?>" /> Days
	  <br />
	  <input type="submit" />
	</form>
	<h3>Get Directions</h3>
	 <form action="https://maps.google.com/maps" method="get">
	   <input type="hidden" name="saddr" value="<?echo urldecode($output[0][1]); ?>" />
	   <input type="hidden" name="daddr" value='<?echo urldecode($output[1][1]);
						    for ($j=2; $j<count($output); $j++) {
						    echo " to:".urldecode($output[$j][1]);
						}
						?>' />
	  <select name="dirflg">
	    <option value="c">Car</option>
	    <option value="w">Walking</option>
	    <option value="b">Fixie</option>
	  </select><br />
	  <input type="submit" />
	</form>
	<h3>Email Page</h3>
	 <form action="email.php" method="post" target="_blank">
	   <input type="hidden" name="events" value="<?echo $_POST['events'] ?>" />
	   <input type="hidden" name="categories" value="<?echo $_POST['categories'] ?>" />
	   <input type="hidden" name="days" value="<?echo $_POST['days'] ?>" />
	   <input type="hidden" name="options" value="<?echo $_POST['options'] ?>" />
	   <input type="hidden" name="distance" value="<?echo $_POST['distance'] ?>" />
	   <input type="hidden" name="location" value="<?echo $_POST['location'] ?>" />
	  <input type="submit" value="Email Me!" />
	</form>
      </div>
      <div class="account">
	<? if ($_SESSION['username'] != null) {?>
	<a href="account.php">
	  <div class="profile_pic" style="background-image: url('<?echo $_SESSION['avatar']; ?>');"></div>
	  <p><?echo $_SESSION['username'];?></p>
	</a>
	<? } else { ?>
	<a href="login.php">
	  <div class="profile_pic"></div>
	  <p>login</p>
	</a>
	<? } ?>
      </div>
	<? if ($_SESSION['username'] != null) {?>
      <div class="logout">
	<div class="profile_pic"></div>
	<a href="logout.php"><p>logout</p></a>
      </div> <?}?>

      <div id="content">
	<div id="map-canvas"></div>
	<h1>My Plan</h1>

	<?php for ($i=0; $i<count($output); $i++) {
	   $info = $output[$i];?>
	      <div class="activity">
		<h2><?php echo urldecode($info[10]).": ".urldecode($info[0]);?></h2>
		<h3><?php echo urldecode($info[1]);?></h3>
		<p><img src="<?php echo urldecode($info[7]);?>" /><?php echo urldecode($info[8]);?></p>
	<?php }?>
	
      </div>
    </div>
  </body>
</html>
