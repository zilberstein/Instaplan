<!DOCTYPE HTML>
<html>
  <head>
    <title>Instaplan</title>  
    <link rel="stylesheet" type="text/css" href="style.css" /><!--
    <script src="planningfont7_400.font.js">-->
  </head>
  <body>
    <div id="container">
      <img src='instaplan.png' width=600px />
      <table id='tb' align="center">
	<tr>
	  <form action="result.php" method="get">
	    <input type="hidden" name="type" value="language" />
	    <td style="text-align:left">
	      <input class="query" placeholder="What would you like to do?" type="text" name="plan" />
	    </td>
	    <td style="text-align:right">
	      <input class="submit" value="Plan It" type="submit" src="button.png" height="50px" />
	    </td>
	  </form>
	</tr>
      </table>
    </div>
  </body>
</html>
