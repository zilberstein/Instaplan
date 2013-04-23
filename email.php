<?
error_reporting(0);
session_start();

//if not arrived through post, go back to index
if($_POST['events']===null)
	header( 'Location: index.php');
if($_SESSION['username']==null)
{
	echo "YOU ARE NOT LOGGED IN! Noam, make this pretty too.\r\n";
	exit;
}
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

$command = "python query.py ".$events." ".$keywords." ".$days." ".$options." ".$distance." ".$lat." ".$long;
$sql= exec($command);

$commands= explode("~",$sql);
$events= explode(",",$_POST['events']);
 
$output= array();
$seen="''";
for($i=0;$i<count($commands);$i++) 
{
	$query= $commands[$i];
	$pos= strpos($query,"order by");
	$query= substr($query,0,$pos)." AND be.businessId NOT IN ($seen) ".substr($query,$pos);
    $result = mysqli_query($db,$query);
    if(mysqli_num_rows($result) == 1) 
	{
      $row = mysqli_fetch_row($result);
	  $seen.=",'$row[9]'";
      $row[count($row)] = $events[$i];
      $output[]=$row;
    }
}

$to = $_SESSION['email'];
//define the subject of the email
$subject ="My Plan - by Instaplan"; 

//create the message
$message= "<html><body><h1>My Plan</h1>";
for ($i=0; $i<count($output); $i++) {
	$info = $output[$i];
	$message.="<div class='activity'>";
	$message.="<h2>".urldecode($info[10]).": ".urldecode($info[0])."</h2>";
	$message.="<h3>".urldecode($info[1])."</h3>";
	$message.="<p><img style='position: relative;float: right;border-radius: 7px;margin-left: 25px;box-shadow: 0 0 8px #000;' src='".urldecode($info[7])."' />";
	$message.=urldecode($info[8])."</p>";
}
$message.="</body></html>";
//define the headers we want passed. Note that they are separated with \r\n
$headers = "From: no-reply@instaplan.com\r\nReply-To: no-reply@instaplan.com\r\nContent-Type: text/html"."\r\n";
//send the email
$mail_sent = @mail( $to, $subject, $message, $headers );
?>
Email Sent. Noam, make this look pretty.