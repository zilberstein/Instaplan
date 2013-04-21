<?php
session_start();
error_reporting(0);

if(!isset($_SESSION['userid'])){
	echo "<center><font face='Verdana' size='2' color=red> 
		Sorry, Please login and use this page </font></center>";
	exit;
}

$db = mysqli_connect("SQL09.FREEMYSQL.NET", "instaplan", "cis330");
/* check connection */
if (mysqli_connect_errno()) {
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
}

mysqli_select_db($db, "instaplan");
$table= "usertest";

/*check if a user is logged in*/
if (!isset($_SESSION['username'])){
	echo "<center><font face='Verdana' size='2' color=red>Sorry, Please login and use this page </font></center>";
	exit;
}
$username = $_SESSION['username'];
$todo = $_POST['todo'];
$old_password = $_POST['old_password']; 
$password=$_POST['password'];
$password2=$_POST['password2'];

if(isset($todo) and $todo=="change-password"){
	$password=mysql_real_escape_string($password);
	$old_password=mysql_real_escape_string($old_password);
}

$status = "OK";
$msg="";


$result = mysqli_query($db,"select * from ".$table." where username='".$username."' and 
	password='".md5($pass)."'");
	
if(!(mysql_num_rows(mysql_query($db, "select * FROM ".$table." WHERE username = '".$username."' 
	and password='".md5($old_password)."'")))){
$msg=$msg."Old Password is not matching<BR>";

$status= "NOTOK";
}

if ( strlen($password) < 3 or strlen($password) > 8 ){
$msg=$msg."Password must be more than 3 char legth and maximum 8 char lenght<BR>";
$status= "NOTOK";}					

if ( $password <> $password2 ){
$msg=$msg."Both passwords are not matching<BR>";
$status= "NOTOK";}		

?>

<!--HTML goes here-->