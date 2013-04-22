<?
session_start();

echo "<script type='text/javascript'>alert('" . $email . "'); </script>";

$to = "sudarshan.muralidhar@gmail.com";
//define the subject of the email
$message = "test";

$subject ="test"; 
//define the headers we want passed. Note that they are separated with \r\n
$headers = "Content-Type: text/html"."\r\n";
//send the email
$mail_sent = @mail( $to, $subject, $message, $headers );

?>