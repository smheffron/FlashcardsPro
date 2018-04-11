<?php
if($data = json_decode(file_get_contents("php://input"), true)){
    $_POST = $data;
}

$response = array("status" => "success");

$newUsername = $_POST['username'];
if(!($newUsername)){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$newPassword = $_POST['password'];
if(!($newPassword)){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);

require('db_credentials.php');

$mysqli = new mysqli($servername, $username, $password, $dbname, $port);
if($mysqli->connect_error){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$stmt = $mysqli->prepare("SELECT * FROM users WHERE username = ?");

if(!($stmt->bind_param("s", $newUsername))){
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
    $response['status'] = 'failed';
    $response['reason'] = 'name taken';
    exit(json_encode($response));
}

$stmt = $mysqli->prepare("INSERT INTO users(username, password) VALUES(?, ?)");

if(!($stmt->bind_param("ss", $newUsername, $hashedPassword))){
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