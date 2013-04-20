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
    </style>

  </head>
  <body>
    <div id="container">
      <img src='images/instaplan.png' width=600px />
      <table id='tb' border="0" cellspacing="0">
	<tr>
	  <form action="result.php" method="get">
	    <input type="hidden" name="type" value="language" />
	    <td style="text-align:left" class="textfield">
	      <input class="query" placeholder="What would you like to do?" type="text" name="plan" />
	    </td>
	    <td style="text-align:right" class="submit_button">
	      <input class="submit" value="Plan It" type="submit" />
	    </td>
	  </form>
	</tr>
      </table>
      <p><a href="login.php">Log in</a> or <a href="register.php">register</a></p>
    </div>
  </body>
</html>
