<?
error_reporting(0);
$db=mysqli_connect("SQL09.FREEMYSQL.NET", "instaplan", "cis330");
/* check connection */
if (mysqli_connect_errno()) {
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
}
mysqli_select_db($db, "instaplan");
$table= "usertest";
$errors = array();

//if already submitted, check values
if($_POST["dispatch"]=="register")
{
	// convert to title case
	$fname= ucwords($_POST["fname"]);
	$lname= ucwords($_POST["lname"]);
	$user=$_POST["user"];
	$email=$_POST["email"];
	$pass=$_POST["pass"];
	$pass2=$_POST["pass2"];
	
	//names can't be empty
	if($fname=="")
		$errors[]= "First name is required";
	if($lname=="")
		$errors[]= "Last name is required";
	//names must be alphabetic
	if(!ctype_alpha($fname))
		$errors[]= "You may only have letters in your first name";
	if(!ctype_alpha($lname))
		$errors[]= "You may only have letters in your last name";
	//userid,pass max size 16, min size 4
	if(strlen($user)<4 || strlen($user)>16)
		$errors[]= "Username must be between 4 and 16 characters";
	if(strlen($pass)<4 || strlen($pass)>16)
		$errors[]= "Password must be between 4 and 16 characters";
	//userid, pass alphanumeric
	if(!ctype_alnum($user))
		$errors[]= "Username must be alphanumeric";
	if(!ctype_alnum($pass))
		$errors[]= "Password must be alphanumeric";
	//passwords have to match
	if($pass!=$pass2)
		$errors[]= "Passwords do not match";
	//check email with regular expressions
	if(!filter_var($email, FILTER_VALIDATE_EMAIL))
		$errors[]= "Invalid email address";
		
	if(count($errors)==0)
	{
		//ensure that username is not taken
		$result = mysqli_query($db,"select * from ".$table." where username='".$user."'");
		//if valid, then insert into database
		if(mysqli_num_rows($result) == 0)
		{
			$sql= "insert into ".$table." (firstname, lastname, username, email, password) values ('";
			$sql.=mysql_real_escape_string($fname)."','";
			$sql.=mysql_real_escape_string($lname)."','";
			$sql.=mysql_real_escape_string($user)."','";
			$sql.=mysql_real_escape_string($email)."','";
			$sql.=mysql_real_escape_string(md5($pass))."')";
			mysqli_query($db,$sql);
			header( 'Location: index.php');
		}
		else
			$errors[]= "Sorry, that username is taken";
	}
}
?>
<!DOCTYPE HTML>
<html>
  <head>
    <title>Instaplan</title>  
    <link rel="stylesheet" type="text/css" href="style.css" /><!--
    <script src="planningfont7_400.font.js">-->
    <style>
      *:focus {
        outline: none;
      }
      p {
        text-align: left;
        margin: 0 80px;
        padding 0;
        color: #77bcdf;
        font-size: .8em;
      }
      p a {
        color: #77bcdf;
        text-decoration: none;
        font-weight: 600;
      }
      p a:hover {
        text-decoration: underline;
      }
      h2 {
        margin: 0;
        padding: 0;
        color: #fff;
      }
    </style>

  </head>
  <body>
    <div id="container">
      <img src='images/instaplan.png' width=600px />
	  <p></p>
	<? if(count($errors)!=0)
	   {
		foreach($errors as $error)
		{
		  echo $error."<br />";
		}
	   }
	?>
		
	
	<form name="input" action="register.php" method="post">
    <table id='login' border="0" cellspacing="0">
	<tr class="login_row first">
	  <td class="label">First Name:</td>
	  <td>
	    <input class="login_field input" type="text" name="fname" value="<?print $_POST["fname"];?>"/>
	  </td>
	</tr>
	<tr class="login_row">
	  <td class="label">Last Name:</td>
	  <td>
	    <input class="login_field input" type="text" name="lname" value="<?print $_POST["lname"];?>"/>
	  </td>
	</tr>
	<tr class="login_row">
	  <td class="label">Username:</td>
	  <td>
	    <input class="login_field input" maxlength="16" type="text" name="user" value="<?print $_POST["user"];?>"/>
	  </td>
	</tr>
	<tr class="login_row">
	  <td class="label">Email:</td>
	  <td>
	    <input class="login_field input" type="text" name="email" value="<?print $_POST["email"];?>"/>
	  </td>
	</tr>
	<tr class="login_row">
	  <td class="label">Password:</td>
	  <td>
	    <input class="login_field input" maxlength="16" type="password" name="pass" />
	  </td>
	</tr>
	<tr class="login_row last">
	  <td class="label">Re-type Password:</td>
	  <td>
	    <input class="login_field input" maxlength="16" type="password" name="pass2" />
	  </td>
	</tr>
	<tr>
	  <td colspan="2">
		<input type="hidden" name="dispatch" value="register"/>
	    <input type="submit" class="submit" value="Register" />
	  </td>
	</tr>
      </table>
      </form>
    </div>
  </body>
</html>
