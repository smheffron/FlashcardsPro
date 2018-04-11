<?php
    require('db_credentials.php');

    $mysqli = new mysqli($servername, $username, $password, $dbname);
    if($mysqli->connect_error){
        die($mysqli->connect_error);
    }

    $sql = "SELECT * FROM users";
    
    if($result = $mysqli->query($sql)){
        if($result->num_rows > 0){
            $testArray = array();
            while($row = $result->fetch_assoc()){
                array_push($testArray, $row);
            }
        }
    }

    $resultString = '';

    foreach($testArray as $row){
        $resultString .= "username: " . $row['username'] . " password: " . $row['password'] . " id: " . $row['id'] . "<br>";
    }

    print($resultString);
?>