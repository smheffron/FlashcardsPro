function verifyLogin(location) {
    $.ajax({
        url: 'php/checkSession.php',
        type: 'post',
        data: {'location': location},
        dataType: 'json',
        success: function(data) {
            console.dir(data);
        }
    });
}