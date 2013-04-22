<!DOCTYPE HTML>
<?
error_reporting(0);
session_start();
/* check if logged in */
if(!isset($_SESSION['username'])){
	header( 'Location: login.php');
	exit;
}

$db=mysqli_connect("sql2.freesqldatabase.com", "sql27018", "tE7!hK3%");
/* check connection */
if (mysqli_connect_errno()) {
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
}
mysqli_select_db($db, "sql27018");
$table= "user";
$email = $_SESSION['email'];
$user = $_SESSION['username'];
$imgurl = $_SESSION['avatar'];
$avatar=false;

$errors = array();
if($_POST["dispatch"]=="update")
{
	$passold= $_POST['passold'];
	$pass = $_POST['pass'];
	$pass2 = $_POST['pass2'];
	$email = $_POST['email'];
	$fpfile=$_POST['fpfile'];
	
	if($fpfile===null || $fpfile=="" || $fpfile=="/" || $fpfile=="//" || $fpfile=="///");
	else
	{
		$fp= str_replace("'","\"",$fpfile);
		$avatar=true;
		$json=json_decode($fp,true);
		$imgurl=$json["url"];
	}
		
	$query="select * from ".$table." where username='".$user."' and password='".md5($passold)."'";
	$result = mysqli_query($db,$query);
	
	if(mysqli_num_rows($result) == 1)
	{
		if($pass != "" || $pass2 != "")
		{
			if(strlen($pass)<4 || strlen($pass)>16)
				$errors[1]= "Password must be between 4 and 16 characters";
			else if(!ctype_alnum($pass))
				$errors[1]= "Password must be alphanumeric";
			//passwords have to match
			else if($pass!=$pass2)
				$errors[1]= "Passwords do not match";
		}
		if(!filter_var($email, FILTER_VALIDATE_EMAIL))
			$errors[2]= "Invalid email address";
			
		if(count($errors)==0)
		{	
			if($pass != "")
			{
				$query="UPDATE $table SET password='".md5($pass)."' WHERE username='$user'";
				mysqli_query($db,$query);
			}
			if($email != $_SESSION['email'])
			{
				$query="UPDATE $table SET email='$email' WHERE username='$user'";
				$_SESSION['email']=$email;
				mysqli_query($db,$query);
			}
			
			if($avatar)
			{
				$query="UPDATE $table SET avatar=1 WHERE username='$user'";
				mysqli_query($db,$query);
				
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
				$_SESSION['avatar']="images/avatars/".$user.".jpg";
			}
			header( 'Location: index.php');
		}
	}
	else
		$errors[0]= "Incorrect Password";
	
}
function display_error($message) {
	echo "<div class='error'>X<div class=\"arrow\"></div><div class=\"message\">";
	echo $message;
	echo "</div></div>";
}
?>

<!--HTML goes here-->

<html>
  <head>
    <title>Instaplan</title>  
	<link rel="icon" href="images/favicon.ico">
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
				var string=JSON.stringify(new_FPFile);
				while(string.indexOf("\"")!==-1){
					string=string.replace("\"","'");
				}
				document.getElementById("fpfile").value = string;
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
      <a href="index.php">
	    <img src='images/instaplan.png' width=600px />		
	</a>	

	    <form name="input" action="account.php" method="post">
	    <table id='login' border="0" cellspacing="0">
	      <tr>
		<td colspan="2">
		<div id="user" >
			<div id="photo"><img src="<?echo $imgurl?>"/></div>
			<h2>My Account</h2>
			<h3><?echo $user?></h3>
			<a href="logout.php" style="text-shadow:2px 2px 4px #000;text-decoration:none;font-weight: normal;">(logout)</a>
			<button type="button" id="attach-photo" style="float:left;"	onclick="getPic()"><?if(strpos($imgurl,"avatar.png")!=false){?>Upload <?}else{?>Change <?}?>Photo</button>
			<input id="fpfile" name="fpfile" class="login_field input" type="hidden" value="<?echo $fpfile?>">
		</div>

		</td>
	      </tr>
	    	<tr><td colspan="2" height="25px">
		<?if (count($errors) != 0){echo "<p class=\"error\">Please fix the following errors:</p>";}?></td></tr>
		
		<tr class="login_row first">
		  <td class="label">Name:</td>
		  <td class="static_data">
		    <?echo $_SESSION["name"]?>
		  </td>
		</tr>
		<tr class="login_row">
		  <td class="label">Username:
		  </td>
		  <td class="static_data">
		    <? echo $user?>
		  </td>
		</tr>
		<tr class="login_row">
		  <td class="label">Password:
		  <? if ($errors[0] != null) {display_error($errors[0]);} ?></td>
		  <td>	
		  <input class="login_field input" maxlength="16" type="password" name="passold" placeholder="Enter Current Password"/>
		  </td>
		</tr>
		<tr class="login_row">
		  <td class="label">New Password:
		  <? if ($errors[1] != null) {display_error($errors[1]);} ?></td>
		  <td>	
		  <input class="login_field input" maxlength="16" type="password" name="pass" placeholder="Enter to Change"/>
		  </td>
		</tr>
		<tr class="login_row">
		  <td class="label">Confirm Password:</td>
		  <td>	
			<input class="login_field input" maxlength="16" type="password" name="pass2" />
		  </td>
		</tr>
		<tr class="login_row last">
		  <td class="label">Email:
		  <? if ($errors[2] != null) {display_error($errors[2]);} ?></td>
		  <td>	
			<input class="login_field input" type="text" name="email" value="<?echo $email?>"/>
		  </td>
		</tr>
		<tr>
		  <td colspan="2">
			<input type="hidden" name="dispatch" value="update"/>
		    <input type="submit" class="submit" value="Update" />
		  </td>
		</tr>
	      </table>
	      </form>
	    
    </div>

  </body>

</html>
