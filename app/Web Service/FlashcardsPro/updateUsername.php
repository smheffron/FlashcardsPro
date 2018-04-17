<?php
/*

URL: http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updateUsername.php

Takes: a user id as $_GET['id']
       the user's new username as $_POST['newUsername']
       the user's password as $_POST['password']

Returns: A JSON object with the key 'status'
         'status' will be 'succeeded' if the username was sucessfully updated or 'failed' if it was not
         
         If the user's password is incorrect, there will also be the key 'reason' with the value 'authentication failure'

*/
if($data = json_decode(file_get_contents("php://input"), true)){
    $_POST = $data;
}

$response = array("status" => "succeeded");

$userId = $_GET['id'] ? $_GET['id'] : -1;
if($userId == -1){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$newUsername = $_POST['newUsername'] ? $_POST['newUsername'] : '';
if(!($newUsername)){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$userPassword = $_POST['password'] ? $_POST['password'] : '';
if(!($userPassword)){
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

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $hashedPassword = $row['password'];
    }
}

if(!password_verify($userPassword, $hashedPassword)){
    $response['status'] = 'failed';
    $response['reason'] = 'authentication failure';
    exit(json_encode($response));
}

// make sure new username is not taken
$stmt = $mysqli->prepare("SELECT * FROM users WHERE username = ?");

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

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

// set username
$stmt = $mysqli->prepare("UPDATE users SET username = ? WHERE id = ?");

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->bind_param("si", $newUsername, $userId))){
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