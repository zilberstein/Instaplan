<?
error_reporting(0);
session_start();

if($_POST["dispatch"]=="query")
{
	$arg = "\"".$_POST["plan"]."\"";
	$command = "python language.py $arg";
	$json = exec($command);
	$keywords = json_decode($json,true);
	if ($keywords["location"] == "") {
	   $error = 1;
	} else if (sizeof($keywords["events"]) == 0) {
	   $error = 2;
	} else {
	  echo "<form name=\"json_poster\" action=\"result.php\" method=\"post\">";
	  $keys = array_keys($keywords);
	  for ($i=0; $i<count($keys); $i++) {
	      $name = $keys[$i];
	      if ($keys[$i] == 'events' || $keys[$i] == 'categories') {
	      	 $value = implode(',',$keywords["$keys[$i]"]);
	      } else {
	        $value = $keywords["$keys[$i]"];
	      }
	      echo "<input type=\"hidden\" name=\"$name\" value=\"$value\" />";
	  }
	  echo "</form><script type=\"text/javascript\">document.json_poster.submit();</script>";
	  }
} else {
  $error = 0;
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
        padding-bottom: 25px;
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
   <?php if ($_POST['dispatch'] == null) {?>
       #container {
           animation:mymove 5s 1s infinite;
           -webkit-animation:mymove 2s 1;
           animation-timing-function:cubic-bezier(0,0,0.25,1);
           -webkit-animation-timing-function:cubic-bezier(0.42,0,0.58,1);
 }<?php }?>


    </style>

  </head>
  <body>
    <div id="container">
      <img src='images/instaplan.png' width=600px />
      <table id='tb' border="0" cellspacing="0">
	<tr>
	  <form action="index.php" method="post">
	    <input type="hidden" name="type" value="language" />
	    <td style="text-align:left" class="textfield">
	      <input class="query" placeholder="What would you like to do?" type="text" name="plan" value="<?php if ($error != 0){echo $_POST['plan'];}?>" autofocus />
	    </td>
	    <td style="text-align:right" class="submit_button">
		  <input type="hidden" name="dispatch" value="query" />
	      <input class="submit" value="Plan It" type="submit" />
	    </td>
	  </form>
	</tr>
      </table>
	  <? if ($error == 0) { 
	  if (!isset($_SESSION['username'])){?>
      <p><a href="login.php">Log in</a> or <a href="register.php">register</a></p>
	  <?} else {?>
	  <p>Hello, <a href="account.php"><?echo $_SESSION['name']?></a> &nbsp;(<a href="logout.php">logout</a>)</p>
	  <?}
	  } else if ($error == 1) {
	    echo "<p class=\"error\">Oops! Your query does not have a location</p>";
	  } else {
	    echo "<p class=\"error\">Oops! How long will you be here?</p>";
	  }?>
    </div>
  </body>
</html>
