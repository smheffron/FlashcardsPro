<?php
/*

URL: http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/verifyLogin.php

Takes: the user's username as $_POST['newUsername']
       the user's password as $_POST['password']

Returns: A JSON object with the keys 'status' and 'login'
         'status' will be 'succeeded' if there were no errors querying the database
         
         'login' will be 'succeeded' if the supplied username/password combination is valid
         
         if 'login' is 'succeeded', there will also be the key 'userId' which will be the id of the user who logged in

*/

header('Access-Control-Allow-Origin: http://flashcardspro.tk', false);

if($data = json_decode(file_get_contents("php://input"), true)){
    $_POST = $data;
}

$response = array("status" => "succeeded", "login" => "failed");

$userLogin = $_POST['username'] ? $_POST['username'] : '';
if(!($userLogin)){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$userPassword= $_POST['password'] ? $_POST['password'] : '';
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

$stmt = $mysqli->prepare("SELECT * FROM users WHERE username = ?");

if(!$stmt){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(!($stmt->bind_param("s", $userLogin))){
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
        $userId = $row['id'];
    }
}
else {
    $response['status'] = 'failed';
    exit(json_encode($response));
}

if(password_verify($userPassword, $hashedPassword)){
    $response['login'] = "succeeded";
    $response['userId'] = $userId;
}

$response['status'] = 'succeeded';
print(json_encode($response));
?>