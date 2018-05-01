function verifyLogin() {
    if((Cookies.get('logged_in') == null || Cookies.get('logged_in') == undefined) && window.location.pathname != '/index.html') {
        window.location = '/index.html';
    }
}

function initLoginPage() {
    $('form').submit(function(e) {
        e.preventDefault();
        $.ajax({
            url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/verifyLogin.php',
            type: 'post',
            dataType: 'json',
            data: {
                    'username': $('#username').val(),
                    'password': $('#password').val()
                  },
            success: function(data) {
                if(data.userId != null && data.status === 'succeeded' && data.login === 'succeeded') {
                    Cookies.set('logged_in', data.userId);
                    window.location = 'home.html';
                }else if(data.status === 'succeeded' && data.login === 'failed') {
                    if($('.errorMessage').length > 0) {
                        $('.errorMessage').text('Incorrect credentials. Please try again.')
                    }else {
                        $('form').before('<p class="errorMessage">Incorrect credentials. Please try again.</p>');
                    }
                }else {
                    if($('.errorMessage').length > 0) {
                        $('.errorMessage').text('There was an error processing your request, please contact the system administrator.')
                    }else {
                        $('form').before('<p class="errorMessage">There was an error processing your request, please contact the system administrator.</p>');
                    }
                }
            },
            error: function(data) {
                if($('.errorMessage').length > 0) {
                    $('.errorMessage').text('There was an error processing your request, please contact the system administrator.')
                }else {
                    $('form').before('<p class="errorMessage">There was an error processing your request, please contact the system administrator.</p>');
                }
            }
        });
    });
}

function initSetsList() {
    $.ajax({
        url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/getSets.php',
        type: 'get',
        dataType: 'json',
        data: {'id': Cookies.get('logged_in')},
        success: function(data) {
            console.dir(data);
            if(data.status === 'succeeded') {
                $('#setsList').append('<ul></ul>');
                $.each(data.sets, function(index, set) {
                    $('#setsList ul').append('<li value="' + set.setId + '">' + set.setName + '</li>');
                });
            }
        }
    });
}