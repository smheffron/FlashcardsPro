<?php
/*

URL: http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/newFlashcard.php

Takes: a set id as $_GET['setId']
       the front text of a card as $_POST['newCardFront']
       the back text of a card as $_POST['newCardBack']

Returns: A JSON object with the keys 'status' and 'newSetId'
         'status' will be 'succeeded' if the card was succesfully inserted into the database or 'failed' if it was not
         
         'newCardId' will be the id of the card in the database

*/

header('Access-Control-Allow-Origin: http://flashcardspro.tk', false);

if($data = json_decode(file_get_contents("php://input"), true)){
    $_POST = $data;
}

$response = array("status" => "succeeded");

$setId = $_GET['setId'] ? $_GET['setId'] : -1;
if($setId == -1){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$newCardFront = $_POST['newCardFront'] ? $_POST['newCardFront'] : '';
if(!$newCardFront){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$newCardBack = $_POST['newCardBack'] ? $_POST['newCardBack'] : '';
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

$stmt = $mysqli->prepare("INSERT INTO cards(setId, frontText, backText) VALUES(?, ?, ?)");

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->bind_param("iss", $setId, $newCardFront, $newCardBack))){
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
            $response['newCardId'] = $row['id'];
        }
    }
}

$response['status'] = 'succeeded';
print(json_encode($response));
?>