<?php
if($data = json_decode(file_get_contents("php://input"), true)){
    $_POST = $data;
}

$response = array("status" => "success");

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