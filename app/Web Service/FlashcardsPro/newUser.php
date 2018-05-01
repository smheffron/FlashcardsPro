<?php
/*

URL: http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/newUser.php

Takes: a username as $_POST['username]
       a user password as $_POST['password']

Returns: A JSON object with the keys 'status' and 'userId'
         'status' will be 'succeeded' if the user was succesfully inserted into the database or 'failed' if they were not
         
         'userId' will be the id of the new user in the database
         
         If the username is already taken, 'status' will be failed and there will also be the key 'reason' with the value 'name taken'
         
*/

header('Access-Control-Allow-Origin: http://flashcardspro.tk', false);

if($data = json_decode(file_get_contents("php://input"), true)){
    $_POST = $data;
}

$response = array("status" => "succeeded");

$newUsername = $_POST['username'] ? $_POST['username'] : '';
if(!($newUsername)){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$newPassword = $_POST['password'] ? $_POST['password'] : '';
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

//check if username taken
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

//create new user
$stmt = $mysqli->prepare("INSERT INTO users(username, password) VALUES(?, ?)");

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->bind_param("ss", $newUsername, $hashedPassword))){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->execute())){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

// getting id of new user
$stmt = $mysqli->prepare("SELECT id FROM users WHERE username = ?");

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
    while($row = $result->fetch_assoc()) {
        $userId = $row['id'];
    }
}

$response['status'] = 'succeeded';
$response['userId'] = $userId;
print(json_encode($response));
?>