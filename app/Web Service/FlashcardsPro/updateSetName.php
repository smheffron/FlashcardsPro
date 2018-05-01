<?php
/*

URL: http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updateSetName.php

Takes: a set id as $_GET['id']
       the new title of the set as $_POST['title']

Returns: A JSON object with the key 'status'
         'status' will be 'succeeded' if the set was succesfully updated or 'failed' if it was not

*/

header('Access-Control-Allow-Origin: http://flashcardspro.tk', false);

if($data = json_decode(file_get_contents("php://input"), true)){
    $_POST = $data;
}

$response = array("status" => "succeeded");

$setId = $_GET['id'] ? $_GET['id'] : -1;
if($setId == -1){
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

$stmt = $mysqli->prepare("UPDATE sets SET name = ? WHERE id = ?");

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->bind_param("si", $newSetTitle, $setId))){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->execute())){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$response['status'] = 'succeeded';
print(json_encode($response));
?>