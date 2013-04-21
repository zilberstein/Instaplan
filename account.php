<?php
session_start();
error_reporting(0);

if(!isset($_SESSION['userid'])){
echo "<center><font face='Verdana' size='2' color=red>
Sorry, Please login and use this page </font></center>";
exit;
}

$db=mysqli_connect("SQL09.FREEMYSQL.NET", "instaplan", "cis330");
/* check connection */
if (mysqli_connect_errno()) {
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
}

mysqli_select_db($db, "instaplan");
$table= "usertest";

$password = $_POST['password']; 



?>

<!--HTML goes here-->