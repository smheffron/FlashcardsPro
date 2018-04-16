<?php
$response = array("status" => "success");

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