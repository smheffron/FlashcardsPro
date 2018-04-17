<?php
/*

URL: http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/getFlashcards.php

Takes: a set id as $_GET['id']

Returns: A JSON object with the key 'status'
         'status' will be 'succeeded' if the cards were succesfully retrieved from the database or 'failed' if they were not
         A JSON array of cards with the key 'cards'
         Each card has the keys 'cardId', 'frontText', and 'backText'

*/
$response = array("status" => "succeeded");

$setId = $_GET['setId'] ? $_GET['setId'] : -1;
if($setId == -1){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

require('db_credentials.php');

$mysqli = new mysqli($servername, $username, $password, $dbname, $port);
if($mysqli->connect_error){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$stmt = $mysqli->prepare("SELECT * FROM cards WHERE setId = ?");

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->bind_param("i", $setId))){
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

$cards = array();
    
while($row = $result->fetch_assoc()) {
    $cardId = $row['id'];
    $frontText = $row['frontText'];
    $backText = $row['backText'];
    $card = array("cardId" => $cardId, "frontText" => $frontText, "backText" => $backText);
    array_push($cards, $card);
}

$response['status'] = 'succeeded';
$response['cards'] = $cards;
print(json_encode($response));
?>