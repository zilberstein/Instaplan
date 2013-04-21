<? 
error_reportion(0);
session_start();

$add = str_replace(" ", "+", $location);

$url='http://maps.googleapis.com/maps/api/geocode/json?address=$add&sensor=false';
$source = file_get_contents($url);
$obj = json_decode($source);
$LATITUDE = $obj->results[0]->geometry->location->lat;
$LONGITUDE = $obj->results[0]->geomerty->location->lng;

echo $LATITUDE;
echo $LONGITUDE;
?>