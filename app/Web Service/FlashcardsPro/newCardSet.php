<?php
/*

URL: http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/newCardSet.php

Takes: a user id as $_GET['id']
       a new set name as $_POST['title']

Returns: A JSON object with the keys 'status' and 'newSetId'
         'status' will be 'succeeded' if the set was succesfully inserted into the database or 'failed' if it was not
         
         'newSetId' will be the id of the set in the database

*/

header('Access-Control-Allow-Origin: http://flashcardspro.tk', false);

if($data = json_decode(file_get_contents("php://input"), true)){
    $_POST = $data;
}

$response = array("status" => "succeeded");

$userId = $_GET['id'] ? $_GET['id'] : -1;
if($userId == -1){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$newSetTitle = $_POST['title'] ? $_POST['title'] : '';
if(!$newSetTitle){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

require('db_credentials.php');

$mysqli = new mysqli($servername, $username, $password, $dbname, $port);
if($mysqli->connect_error){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$stmt = $mysqli->prepare("INSERT INTO sets(ownerId, name) VALUES(?, ?)");

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->bind_param("is", $userId, $newSetTitle))){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->execute())){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$sql = "SELECT LAST_INSERT_ID() AS id";
    
if($result = $mysqli->query($sql)){
    if($result->num_rows > 0){
        while($row = $result->fetch_assoc()){
            $response['newSetId'] = $row['id'];
        }
    }
}

$response['status'] = 'succeeded';
print(json_encode($response));
?>