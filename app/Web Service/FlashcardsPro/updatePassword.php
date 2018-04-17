<?php
/*

URL: http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updatePassword.php

Takes: a user id as $_GET['id']
       the user's old password as $_POST['oldPassword']
       the user's new password as $_POST['newPassword']

Returns: A JSON object with the key 'status'
         'status' will be 'succeeded' if the password was sucessfully updated or 'failed' if it was not
         
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

if(!password_verify($oldPassword, $hashedPassword)){
    $response['status'] = 'failed';
    $response['reason'] = 'authentication failure';
    exit(json_encode($response));
}

// set password
$hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);
    
$stmt = $mysqli->prepare("UPDATE users SET password = ? WHERE id = ?");

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

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