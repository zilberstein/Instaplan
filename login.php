<?
// Inialize session
session_start();

error_reporting(0);
$db=mysqli_connect("SQL09.FREEMYSQL.NET", "instaplan", "cis330");
/* check connection */
if (mysqli_connect_errno()) {
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
}
mysqli_select_db($db, "instaplan");
$table= "usertest";

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
		$_SESSION['username']=$user;
		header( 'Location: index.php');
	}
	else
	{
		$_SESSION['username']=null;
	}
}
if (isset($_SESSION['username']))
	echo "<script type='text/javascript'>alert('".$_SESSION['username']."');</script>";
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
	  <?if($_POST["dispatch"]=="login")
			echo "<br>INVALID LOGIN!";
	  ?>
	  <form name="login" action="login.php" method="post">
      <table id='login' border="0" cellspacing="0">
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
	  </td>
	</tr>
      </table>
      </form>
    </div>
  </body>
</html>
