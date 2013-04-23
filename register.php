<?
error_reporting(0);
session_start();

if ($_SESSION['username']!=null)
	header( 'Location: account.php');

$db=mysqli_connect("SQL09.FREEMYSQL.NET", "instaplan", "cis330");
/* check connection */
if (mysqli_connect_errno()) {
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
}
mysqli_select_db($db, "instaplan");
$table= "user";
$errors = array();
$avatar=false;
$imgurl="";
$fpfile="";

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
	
	if($fpfile===null || $fpfile=="" || $fpfile=="/" || $fpfile=="//" || $fpfile=="///")
	{
		$fpfile="";
		$avatar=false;
	}
	else
	{
		$fp= str_replace("'","\"",$fpfile);
		$avatar=true;
		$json=json_decode($fp,true);
		$imgurl=$json["url"];
	}
	
	//names can't be empty
	if($fname=="")
		$errors[0]= "First name is required";
	//names must be alphabetic
	else if(!ctype_alpha($fname))
		$errors[0]= "You may only have letters in your first name";
	if($lname=="")
		$errors[1]= "Last name is required";
	else if(!ctype_alpha($lname))
		$errors[1]= "You may only have letters in your last name";
	//userid,pass max size 16, min size 4
	if(strlen($user)<4 || strlen($user)>16)
		$errors[2]= "Username must be between 4 and 16 characters";
	//userid, pass alphanumeric
	else if(!ctype_alnum($user))
		$errors[2]= "Username must be alphanumeric";
	if(strlen($pass)<4 || strlen($pass)>16)
		$errors[4]= "Password must be between 4 and 16 characters";
	else if(!ctype_alnum($pass))
		$errors[4]= "Password must be alphanumeric";
	//passwords have to match
	else if($pass!=$pass2)
		$errors[4]= "Passwords do not match";
	//check email with regular expressions
	if(!filter_var($email, FILTER_VALIDATE_EMAIL))
		$errors[3]= "Invalid email address";
		
	if(count($errors)==0)
	{
		//ensure that username is not taken
		$result = mysqli_query($db,"select * from ".$table." where username='".$user."'");
		//if valid, then insert into database
		if(mysqli_num_rows($result) == 0)
		{
			$sql= "insert into ".$table." (firstname, lastname, username, email, password, avatar) values ('";
			$sql.=mysqli_real_escape_string($db,$fname)."','";
			$sql.=mysqli_real_escape_string($db,$lname)."','";
			$sql.=mysqli_real_escape_string($db,$user)."','";
			$sql.=mysqli_real_escape_string($db,$email)."','";
			$sql.=mysqli_real_escape_string($db,md5($pass))."',";
			if($avatar)
				$sql.="1)";
			else
				$sql.="0)";
			echo "<script type='text/javascript'>alert('".$sql."');</script>";
			mysqli_query($db,$sql);
			
			if($avatar)
			{
				//store file locally, and delete it from filepicker
				copy($imgurl, "images/avatars/".$user.".jpg");
				chmod("images/avatars/".$user.".jpg", 0705); 
				//delete
				$ch = curl_init();
				// set URL and other appropriate options
				curl_setopt($ch, CURLOPT_URL, $imgurl);
				curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "DELETE");
				// grab URL and pass it to the browser
				curl_exec($ch);
				// close cURL resource, and free up system resources
				curl_close($ch);
			}
			
			//log in, and redirect to home
			$_SESSION['username']=$user;
			$_SESSION['name']=$fname." ".$lname;
			$_SESSION['email']=$email;
			if($avatar)
			{
				$_SESSION['avatar']="images/avatars/".$user.".jpg";
			}
			else
			{
				$_SESSION['avatar']="images/avatars/avatar.png";
			}
			header( 'Location: index.php');
		}
		else
			$errors[2]= "Sorry, that username is taken";
	}
}
function display_error($message) {
	echo "<div class='error'>X<div class=\"arrow\"></div><div class=\"message\">";
	echo $message;
	echo "</div></div>";
}
?>
<!DOCTYPE HTML>
<html>
  <head>
    <title>Instaplan</title>  
	<link rel="icon" href="images/favicon.ico">
    <link rel="stylesheet" type="text/css" href="style.css" />
	<script type="text/javascript" src="//api.filepicker.io/v1/filepicker.js"></script>
	<script type="text/javascript">
	filepicker.setKey("AzauR2MBSBeslVoQIcZ8gz");
	var oldFpFile=null;
	function getPic()
	{
		filepicker.pickAndStore({mimetype: 'image/*'},{},function(fpfiles){
			if(oldFpFile!=null)
				filepicker.remove(oldFpFile);
			document.getElementById("photo").innerHTML = "Converting...";
			filepicker.convert(fpfiles[0], {width: 100, height: 100},
				function(new_FPFile){
				document.getElementById("photo").innerHTML = "<img src="+new_FPFile.url+" height=\"30px\"></img>";
				var string=JSON.stringify(new_FPFile);
				while(string.indexOf("\"")!==-1){
					string=string.replace("\"","'");
				}
				document.getElementById("fpfile").value = string;
				oldFpFile=new_FPFile;
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
        text-shadow: 1px 1px 2px #000;
      }
      p a {
        color: #77bcdf;
        text-decoration: none;
        font-weight: 600;
      }
      p a:hover {
        text-decoration: underline;
      }
        p.error {
          color: #ffac0d;
          text-align: left;
          margin: 0;
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
	background-image: url('images/button_grad.png');
	width: 20px;
	height: 20px;
	text-align: center;
	border-radius: 10px;
	text-shadow: none;
	font-weight: normal;
	}
     div.error div.message {
	position: relative;
	visibility:none;
	display: none;
	background-color: rgba(0,0,0,.5);
	z-index: 10;
	top: -60px;
	left: 30px;
	font-size: .75em;
	padding: 5px;
	border-radius: 5px;
	font-weight: normal;
	width: 150px;
	height: 50px;
	text-align: left;
      }
	div.error:hover div {
	visibility:visible;
	display: block;
      }
      div.arrow {
      	visibility: hidden;
	display: none;
        opacity: .5;
	position: relative;
	top: -20px;
	left: 20px;
        width: 0; 
        height: 0; 
        border-top: 10px solid transparent;
        border-bottom: 10px solid transparent; 
        border-right:10px solid black; 
}    
     #attach-photo {
       background-color: #f00;
       background-image: url('images/button_grad.png');
       border: 0;
       border-radius: 7px;
       margin: 2px;;
       height: 30px;
       color: #fff;
}

</style>

  </head>
  <body>
    <div id="container">
     <a href="index.php"> <img src='images/instaplan.png' width=600px /></a>
	
	<form name="input" action="register.php" method="post">
    <table id='login' border="0" cellspacing="0">
    	<tr><td colspan="2" height="25px">
	<?php if (count($errors) != 0){echo "<p class=\"error\">Please fix the following errors:</p>";}?></td></tr>
	<tr class="login_row first">
	  <td class="label">First Name:
	  <?php if ($errors[0] != null) {display_error($errors[0]);} ?>
	  </td>
	  <td>
	    <input class="login_field input" type="text" name="fname" value="<?print $_POST["fname"];?>"/>
	  </td>
	</tr>
	<tr class="login_row">
	  <td class="label">Last Name:
	  <?php if ($errors[1] != null) {display_error($errors[1]);} ?></td>
	  <td>
	    <input class="login_field input" type="text" name="lname" value="<?print $_POST["lname"];?>"/>
	  </td>
	</tr>
	<tr class="login_row">
	  <td class="label">Username:
	  <?php if ($errors[2] != null) {display_error($errors[2]);} ?></td>
	  <td>
	    <input class="login_field input" maxlength="16" type="text" name="user" value="<?print $_POST["user"];?>"/>
	  </td>
	</tr>
	<tr class="login_row">
	  <td class="label">Email:
	  <?php if ($errors[3] != null) {display_error($errors[3]);} ?></td>
	  <td>
	    <input class="login_field input" type="text" name="email" value="<?print $_POST["email"];?>"/>
	  </td>
	</tr>
	<tr class="login_row">
	  <td class="label">Password:
	  <?php if ($errors[4] != null) {display_error($errors[4]);} ?></td>
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
	    <button type="button" id="attach-photo" style="float:left;"		onclick="getPic()"><?if (!$avatar){?>Upload <?}else{?>Change <?}?>Photo</button>
		<div id="photo"><img src="<?echo $imgurl?>" height="30px" /></div>
		<input id="fpfile" name="fpfile" class="login_field input" type="hidden" value="<?echo $fpfile?>"/>
	  </td>
	</tr>
	<tr>
	  <td colspan="2">
		<input type="hidden" name="dispatch" value="register"/>
	    <input type="submit" class="submit" value="Register" />
	  or <a href="login.php">log in</a>
</p>
	  </td>
	</tr>
      </table>
      </form>
    </div>
  </body>
</html>
