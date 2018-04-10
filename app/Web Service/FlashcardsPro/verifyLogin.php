<?php
if($data = json_decode(file_get_contents("php://input"), true)){
    $_POST = $data;
}

$response = array("status" => "success", "login" => "failed");

$userLogin = $_POST['username'];
if(!($userLogin)){
    $response['status'] = 'failed';
    exit(json_encode($response));
}

$userPassword= $_POST['password'];
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

if(password_verify($userPassword, $hashedPassword)){
    $response['login'] = "succeeded";
    $response['userId'] = $userId;
}

$response['status'] = 'succeeded';
print(json_encode($response));
?>