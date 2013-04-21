<?
error_reporting(0);
$db=mysql_connect("SQL09.FREEMYSQL.NET", "instaplan", "cis330")
  or die('I cannot connect to the database because: ' . mysql_error());
mysql_select_db("instaplan", $db);
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
		<input type="hidden" name="submit" value="login"/>
	    <input type="submit" class="submit" value="Log in" />
	  </td>
	</tr>
      </table>
      </form>
    </div>
  </body>
</html>
