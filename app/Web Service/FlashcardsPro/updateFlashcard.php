<?php
if($data = json_decode(file_get_contents("php://input"), true)){
    $_POST = $data;
}

$response = array("status" => "success");

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