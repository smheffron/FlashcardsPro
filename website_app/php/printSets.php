<?php
    require('db_credentials.php');

    $mysqli = new mysqli($servername, $username, $password, $dbname);
    if($mysqli->connect_error){
        die($mysqli->connect_error);
    }

    $sql = "SELECT * FROM sets";
    
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
        $resultString .= "id: " . $row['id'] . " owned by: " . $row['ownerId'] . " name: " . $row['name'] . "<br>";
    }

    print($resultString);
?>