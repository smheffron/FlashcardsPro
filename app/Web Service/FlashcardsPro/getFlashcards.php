<?php
$response = array("status" => "success");

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