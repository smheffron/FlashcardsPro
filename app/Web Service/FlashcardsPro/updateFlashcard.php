<?php
/*

URL: http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updateFlashcard.php

Takes: a card id as $_GET['cardId']
       the new front text of the card as $_POST['newFront']
       the new back text of the card as $_POST['newBack']

Returns: A JSON object with the key 'status'
         'status' will be 'succeeded' if the card was succesfully updated or 'failed' if it was not

*/
if($data = json_decode(file_get_contents("php://input"), true)){
    $_POST = $data;
}

$response = array("status" => "succeeded");

$cardId = $_GET['cardId'] ? $_GET['cardId'] : -1;
if($cardId == -1){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$newCardFront = $_POST['newFront'] ? $_POST['newFront'] : '';
if(!$newCardFront){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$newCardBack = $_POST['newBack'] ? $_POST['newBack'] : '';
if(!$newCardBack){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

require('db_credentials.php');

$mysqli = new mysqli($servername, $username, $password, $dbname, $port);
if($mysqli->connect_error){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$stmt = $mysqli->prepare("UPDATE cards SET frontText = ?, backText = ? WHERE id = ?");

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->bind_param("ssi", $newCardFront, $newCardBack, $cardId))){
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