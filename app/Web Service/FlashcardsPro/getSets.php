<?php
/*

URL: http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/getSets.php

Takes: a user id as $_GET['id']

Returns: A JSON object with the key 'status'
         'status' will be 'succeeded' if the sets were succesfully retrieved from the database or 'failed' if they were not
         A JSON array of sets with the key 'sets'
         Each set has the keys 'setName' and 'setId'

*/

header('Access-Control-Allow-Origin: http://flashcardspro.tk', false);

$response = array("status" => "succeeded");

$userId = $_GET['id'] ? $_GET['id'] : -1;
if($userId == -1){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

require('db_credentials.php');

$mysqli = new mysqli($servername, $username, $password, $dbname, $port);
if($mysqli->connect_error){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$stmt = $mysqli->prepare("SELECT * FROM sets WHERE ownerId = ?");

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->bind_param("i", $userId))){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->execute())){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($result = $stmt->get_result())){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$sets = array();
    
while($row = $result->fetch_assoc()) {
    $setName = $row['name'];
    $setId = $row['id'];
    $set = array("setName" => $setName, "setId" => $setId);
    array_push($sets, $set);
}

$response['status'] = 'succeeded';
$response['sets'] = $sets;
print(json_encode($response));
?>