<?php
/*

URL: http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/deleteFlashcard.php

Takes: a card id as $_GET['cardId']

Returns: A JSON object with the key 'status'
         'status' will be 'succeeded' if the card was succesfully removed from the database or 'failed' if it was not

*/

header('Access-Control-Allow-Origin: http://flashcardspro.tk', false);

$response = array("status" => "succeeded");

$cardId = $_GET['cardId'] ? $_GET['cardId'] : -1;
if($cardId == -1){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

require('db_credentials.php');

$mysqli = new mysqli($servername, $username, $password, $dbname, $port);
if($mysqli->connect_error){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$stmt = $mysqli->prepare("DELETE FROM cards WHERE id = ?");

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->bind_param("i", $cardId))){
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