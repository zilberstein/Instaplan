<?
error_reporting(0);
// Inialize session
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

if($_POST["dispatch"]=="login")
{
	$login=true;
	
	$user=$_POST["user"];
	$pass=$_POST["pass"];
	
	//userid,pass max size 16, min size 4
	if(strlen($user)<4 || strlen($user)>16)
		$login=false;
	if(strlen($pass)<4 || strlen($pass)>16)
		$login=false;
	//userid, pass alphanumeric
	if(!ctype_alnum($user))
		$login=false;
	if(!ctype_alnum($pass))
		$login=false;
		
	$result = mysqli_query($db,"select * from ".$table." where username='".$user."' and password='".md5($pass)."'");
	if(mysqli_num_rows($result) == 1)
	{
		$val = mysqli_fetch_row ($result);
		$_SESSION['username']=$user;
		$_SESSION['name']=$val[0]." ".$val[1];
		$_SESSION['email']=$val[3];
		if($val[5]=='1')
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
	{
		$_SESSION['username']=null;
		$_SESSION['name']=null;
		$_SESSION['email']=null;
		$_SESSION['avatar']=null;
	}
}
?>
<!DOCTYPE HTML>
<html>
  <head>
    <title>Instaplan</title>  
	<link rel="icon" href="images/favicon.ico">
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
      h2 {
        margin: 0;
        padding: 0;
        color: #fff;
      }
      p.error {
        color: #ffac0d;
	text-align: left;
	margin: 0;
	}
    </style>

  </head>
  <body>
    <div id="container">
     <a href="https://fling.seas.upenn.edu/~noamz/cgi-bin/instaplan/"><img src='images/instaplan.png' width=600px /></a>
	  <form name="login" action="login.php" method="post">
      <table id='login' border="0" cellspacing="0">
	<tr>
	<td colspan="2" height="25px" align="left">
	<?if($_POST["dispatch"]=="login")
			echo "<p class='error'>Invalid username and/or password</p>";
	  ?>
	</td>
	</tr>	
	<tr class="login_row first">
	  <td class="label">Username:</td>
	  <td>
	    <input class="login_field input" maxlength="16" type="text" name="user" value="<?print $_POST["user"];?>"/>
	  </td>
	</tr>
	<tr class="login_row last">
	  <td class="label">Password:</td>
	  <td>
	    <input class="login_field input" maxlength="16" type="password" name="pass" />
	  </td>
	</tr>
	<tr>
	  <td colspan="2">
		<input type="hidden" name="dispatch" value="login"/>
	    <input type="submit" class="submit" value="Log in" />
	    	  or <a href="register.php">register</a>
	  </td>
	</tr>
      </table>
      </form>
    </div>
  </body>
</html>
