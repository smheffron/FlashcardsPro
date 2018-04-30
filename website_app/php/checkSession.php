<?php
    if(!session_start()) {
		// If the session couldn't start, present an error
		$json = array('error' => 'Could not start the session. Please contact the network administrator.',
                      'status' => 'failed'
                     );
        $json = json_encode($json);
        echo $json;
		die();
	}

    $location = empty($_POST['location']) ? '' : $_POST['location'];
    
    if(isset($_SESSION['logged_in']) and $location != 'login') {
        $user = $_SESSION['logged_in'];
        $json = array('status' => 'success',
                      'user' => $user
                     );
        $json = json_encode($json);
        echo $json;
    }else if(isset($_SESSION['logged_in']) and $location === 'login'){
        $user = $_SESSION['logged_in'];
        $json = array('status' => 'success',
                      'redirect' => "/home.html",
                      'user' => $user
                     );
        $json = json_encode($json);
        echo $json;
    }else if(!isset($_SESSION['logged_in']) and $location != 'login'){
        $json = array('status' => 'failed',
                      'redirect' => "/"
                     );
        $json = json_encode($json);
        echo $json;
    }else {
        $json = array('status' => 'success',
                      'user' => null
                     );
        $json = json_encode($json);
        echo $json;
    }
?>