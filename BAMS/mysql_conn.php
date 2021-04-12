<?php

$servername = "cslinux0.comp.hkbu.edu.hk";
$username = "comp4107_grp12";
$password = "188284";
$dbname = "comp4107_grp12";

// Create Connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Connection check
if ($conn->connect_error) {
    die("Connection Fail: " . $conn->connect_error);
} 

?>