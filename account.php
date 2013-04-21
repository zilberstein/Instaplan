<?php
session_start();
error_reporting(0);
/* check if logged in */
if(!isset($_SESSION['userid'])){
	header( 'Location: index.php');
	exit;
}

$db = mysqli_connect("SQL09.FREEMYSQL.NET", "instaplan", "cis330");
/* check connection */
if (mysqli_connect_errno()) {
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
}
mysqli_select_db($db, "instaplan");
$table= "user";
$errors = array();

$username = $_SESSION['username'];
//$old_password = $_POST['old_password']; 
if($_POST["dispatch"] == "register"){
	$password = $_POST['password'];
	$password2 = $_POST['password2']; //confirm
}

//$password=mysql_real_escape_string($password);

//	$old_password=mysql_real_escape_string($old_password);


//checking if you know the old password --> implement later	
/*if(!(mysql_num_rows(mysql_query($db, "select * FROM ".$table." WHERE username = '".$username."' 
	and password='".md5($old_password)."'")))){
$msg=$msg."Old Password is not matching<BR>";
$errors[1] = "something";
}*/

/*check if password == password2, and if it conforms length constraint*/
if ( strlen($password) < 4 || strlen($password) > 16 ){
	$errors[0] = "Password must be between 4 and 16 characters";
}
else if(!ctype_alnum($password)){
	$errors[0] = "Password must be alphanumeric";
}
else if( $password <> $password2){
	$errors[0] = "Passwords do not match";
}

if(count($errors)==0){
	$sql= "update ".$table." set password = '";
	$sql.=mysqli_real_escape_string($db,md5($password))."' where username = '".$username."'";
	
	echo "<script type='text/javascript'>alert('".$sql."');</script>";
	mysqli_query($db,$sql);
}

?>

<!--HTML goes here-->

<!DOCTYPE HTML>
<html>
  <head>
    <title>Instaplan</title>  
    <link rel="stylesheet" type="text/css" href="style.css" />
	<style>

	</style>
  </head>
  <body>
  
    <div id="container">
	    <img src='images/instaplan.png' width=600px />		
		
		<form name="input" action="account.php" method="post">
	    <table id='account' border="0" cellspacing="0">
	    	<tr><td colspan="2" height="25px">
		<?php if (count($errors) != 0){echo "<p class=\"error\">Please fix the following errors:</p>";}?></td></tr>
		
		<tr class="account_row">
		  <td class="label">Password:
		  <?php if ($errors[0] != null) {display_error($errors[0]);} ?></td>
		  <td>
		    <input class="account_field input" maxlength="16" type="password" name="password" />
		  </td>
		</tr>
		<tr class="account_row">
		  <td class="label">Confirm Password:</td>
		  <td>
		    <input class="account_field input" maxlength="16" type="password" name="password2" />
		  </td>
		</tr>

		<tr>
		  <td colspan="2">
			<input type="hidden" name="dispatch" value="Update Account"/>
		    <input type="submit" class="submit" value="Update Account" />
		  </td>
		</tr>
	      </table>
	      </form>
	    
    </div>

  </body>

</html>
