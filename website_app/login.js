function initLoginPage() {
    $('#loginForm form').submit(function(e) {
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
                    Cookies.set('username', $('#username').val());
                    window.location = '/home.html';
                }else if(data.status === 'succeeded') {
                    if($('.errorMessage').length > 0) {
                        $('.errorMessage').text('Incorrect credentials. Please try again.')
                    }else {
                        $('#loginForm').before('<p class="errorMessage">Incorrect credentials. Please try again.</p>');
                    }
                }else {
                    if($('.errorMessage').length > 0) {
                        $('.errorMessage').text('There was an error processing your request, please contact the system administrator.')
                    }else {
                        $('#loginForm').before('<p class="errorMessage">There was an error processing your request, please contact the system administrator.</p>');
                    }
                }
            },
            error: function(data) {
                if($('.errorMessage').length > 0) {
                    $('.errorMessage').text('There was an error processing your request, please contact the system administrator.')
                }else {
                    $('#loginForm').before('<p class="errorMessage">There was an error processing your request, please contact the system administrator.</p>');
                }
            }
        });
    });
    
    $('#createAccountForm form').submit(function(e) {
        e.preventDefault();
        if($('#newUserPassword').val() === $('#newUserConfirmPassword').val()) {
            $.ajax({
                url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/newUser.php',
                type: 'post',
                dataType: 'json',
                data: {
                        'username': $('#newUserUsername').val(),
                        'password': $('#newUserPassword').val()
                      },
                success: function(data) {
                    if(data.userId != null && data.status === 'succeeded') {
                        Cookies.set('logged_in', data.userId);
                        Cookies.set('username', $('#newUserUsername').val());
                        window.location = '/home.html';
                    }else if(data.reason === 'name taken') {
                        if($('.errorMessage').length > 0) {
                            $('.errorMessage').text('Username taken. Please try a different one.')
                        }else {
                            $('#createAccountForm').before('<p class="errorMessage">Username taken. Please try a different one.</p>');
                        }
                        $('#newUserUsername, #newUserPassword, #newUserConfirmPassword').val('');
                    }else {
                        console.dir(data);
                        if($('.errorMessage').length > 0) {
                            $('.errorMessage').text('There was an error processing your request, please contact the system administrator.')
                        }else {
                            $('#createAccountForm').before('<p class="errorMessage">There was an error processing your request, please contact the system administrator.</p>');
                        }
                        $('#newUserPassword, #newUserConfirmPassword').val('');
                    }
                },
                error: function(data) {
                    console.dir(data);
                    if($('.errorMessage').length > 0) {
                        $('.errorMessage').text('There was an error processing your request, please contact the system administrator.')
                    }else {
                        $('#createAccountForm').before('<p class="errorMessage">There was an error processing your request, please contact the system administrator.</p>');
                    }
                    $('#newUserPassword, #newUserConfirmPassword').val('');
                }
            });
        }else {
            if($('.errorMessage').length > 0) {
                $('.errorMessage').text('The passwords did not match.')
            }else {
                $('#createAccountForm').before('<p class="errorMessage">The passwords did not match.</p>');
            }
            $('#newUserPassword, #newUserConfirmPassword').val('');
        }
    });
}

function initCreateAccount(obj) {
    $('#loginForm').hide();
    $('#createAccountForm').show();
    $(obj).hide();
}