<?php
/*

URL: http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/deleteUser.php

Takes: a user id as $_GET['id']
       a user password as $_POST['password']

Returns: A JSON object with the key 'status'
         'status' will be 'succeeded' if the user was succesfully removed from the database or 'failed' if they were not
         
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

if($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $hashedPassword = $row['password'];
    }
}

if(!password_verify($userPassword, $hashedPassword)){
    $response['status'] = 'failed';
    $response['reason'] = 'authentication failure';
    exit(json_encode($response));
}

// Delete all cards and sets belonging to user
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

if($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $setId = $row['id'];
        $stmt = $mysqli->prepare("DELETE FROM cards WHERE setId = ?");

        if(!$stmt){
            $response['status'] = 'failed';
            exit(json_encode($response));
        }

        if(!($stmt->bind_param("i", $setId))){
            $response['status'] = 'failed';
            exit(json_encode($response));
        }

        if(!($stmt->execute())){
            $response['status'] = 'failed';
            exit(json_encode($response));
        }
        
        $stmt = $mysqli->prepare("DELETE FROM sets WHERE id = ?");

        if(!$stmt){
            $response['status'] = 'failed';
            exit(json_encode($response));
        }

        if(!($stmt->bind_param("i", $setId))){
            $response['status'] = 'failed';
            exit(json_encode($response));
        }

        if(!($stmt->execute())){
            $response['status'] = 'failed';
            exit(json_encode($response));
        }
    }
}

// delete user
$stmt = $mysqli->prepare("DELETE FROM users WHERE id = ?");

if(!$stmt){
    $response['status'] = 'failed to prepare 2';
    exit(json_encode($response));
}

if(!($stmt->bind_param("i", $userId))){
    $response['status'] = 'failed to bind 2';
    exit(json_encode($response));
}

if(!($stmt->execute())){
    $response['status'] = 'failed to execute 2';
    exit(json_encode($response));
}

$response['status'] = 'succeeded';
print(json_encode($response));
?>