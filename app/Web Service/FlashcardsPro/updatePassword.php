<?php
if($data = json_decode(file_get_contents("php://input"), true)){
    $_POST = $data;
}

$response = array("status" => "success");

$userId = $_GET['id'] ? $_GET['id'] : -1;
if($userId == -1){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$oldPassword = $_POST['oldPassword'] ? $_POST['oldPassword'] : '';
if(!($oldPassword)){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$newPassword = $_POST['newPassword'] ? $_POST['newPassword'] : '';
if(!($newPassword)){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

require('db_credentials.php');

$mysqli = new mysqli($servername, $username, $password, $dbname, $port);
if($mysqli->connect_error){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

// Authenticate user
$stmt = $mysqli->prepare("SELECT * FROM users WHERE id = ?");

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

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $hashedPassword = $row['password'];
    }
}

if(!password_verify($oldPassword, $hashedPassword)){
    $response['status'] = 'failed';
    $response['reason'] = 'authentication failure';
    exit(json_encode($response));
}

// set password
$hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);
    
$stmt = $mysqli->prepare("UPDATE users SET password = ? WHERE id = ?");

if(!($stmt->bind_param("si", $hashedPassword, $userId))){
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