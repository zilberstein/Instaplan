<?
error_reporting(0);
session_start();
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
	$fpfile=$_POST["fpfile"];
	
	$json=json_decode($fpfile,true);
	$imgurl=$json["url"];
	
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
			$sql.=mysqli_real_escape_string($fname)."','";
			$sql.=mysqli_real_escape_string($lname)."','";
			$sql.=mysqli_real_escape_string($user)."','";
			$sql.=mysqli_real_escape_string($email)."','";
			$sql.=mysqli_real_escape_string(md5($pass))."')";
			mysqli_query($db,$sql);
			
			//store file locally, and delete it from filepicker
			file_put_contents("https://fling.seas.upenn.edu/~smural/cgi-bin/Instaplan/images/".$user.".jpg", $imgurl); 
			//delete
			$ch = curl_init();
			// set URL and other appropriate options
			curl_setopt($ch, CURLOPT_URL, $imgurl);
			curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "DELETE");

			// grab URL and pass it to the browser
			curl_exec($ch);

			// close cURL resource, and free up system resources
			curl_close($ch);
			
			//log in, and redirect to home
			$_SESSION['username']=$user;
			$_SESSION['name']=$fname." ".$lname;
			$_SESSION['email']=$email;
			header( 'Location: index.php');
		}
		else
			$errors[]= "Sorry, that username is taken";
	}
}
function display_error(&$message) {
	echo "<div class='error'>X<div>";
	echo $message;
	echo "</div></div>";
}
?>
<!DOCTYPE HTML>
<html>
  <head>
    <title>Instaplan</title>  
    <link rel="stylesheet" type="text/css" href="style.css" />
	<script type="text/javascript" src="//api.filepicker.io/v1/filepicker.js"></script>
	<script type="text/javascript">
	filepicker.setKey("AzauR2MBSBeslVoQIcZ8gz");
	function getPic()
	{
		filepicker.pickAndStore({mimetype: 'image/*'},{},function(fpfiles){
			document.getElementById("photo").innerHTML = "Converting...";
			filepicker.convert(fpfiles[0], {width: 100, height: 100},
				function(new_FPFile){
				document.getElementById("photo").innerHTML = "<img src="+new_FPFile.url+"></img>";
				document.getElementById("fpfile").value = JSON.stringify(new_FPFile);
				filepicker.remove(fpfiles[0]);
			});
			document.getElementById("attach-photo").innerHTML = "Change Photo";
		});
	}
	</script>
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
      div.error {
        float:right;
	color: #eee;
	background-color: #f00;
	width: 20px;
	height: 20px;
	text-align: center;
	border-radius: 10px;
	text-shadow: none;
	}
	div.error div {
	position: relative;
	visibility:none;
	display: none;
	background-color: #f00;
	z-index: 10;
	top: -20px;
	left: 20px;
	font-size: .8em;
	padding: 15px;
	width: 200px;
	}
	div.error:hover div {
	visibility:visible;
	display: block;
	background-color: #000;
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
	  <td class="label">First Name:
	  <?php
	    $message = "You fucked up";
	    display_error($message);?>
	  </td>
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
	<tr class="login_row">
	  <td class="label">Re-type Password:</td>
	  <td>
	    <input class="login_field input" maxlength="16" type="password" name="pass2" />
	  </td>
	</tr>
	<tr class="login_row last button">
	  <td class="label">Avatar:</td>
	  <td>
	    <button class="login_field input" type="button" id="attach-photo" style="float:left;"		onclick="getPic()"><?if (!isset ($fpfile)){?>Upload <?}else{?>Change <?}?>Photo</button>
		<div id="photo"><img src="<?echo $imgurl?>"></img></div>
		<input id="fpfile" name="fpfile" class="login_field input" type="hidden" value=<?echo $fpfile?>/>
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
