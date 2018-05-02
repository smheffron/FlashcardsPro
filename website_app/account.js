function initUserData() {
    var username = Cookies.get('username');
    
    $('#userDataWrapper div').html('<b>Username:</b> ' + username);
}

function initEditUsername() {
    var id = Cookies.get('logged_in');
    var username = Cookies.get('username');
    
    var formSkeleton = '<label for="newUsername">New Username: </label><br>'
                     + '<input type="text" id="newUsername" placeholder="New Username..." value="' + username + '"/><br>'
                     + '<input type="password" id="password" placeholder="Password..." /><br>'
                     + '<button class="btn btn-primary" onclick="editUsername(' + id + ')">Update</button>';
    $('#account').html(formSkeleton);
    $('#editPasswordBtn, #editUsernameBtn, #deleteUserBtn').addClass('disabled');
}

function editUsername(id) {
    var newUsername = $('#newUsername').val();
    var password = $('#password').val();
    
    $.ajax({
        url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updateUsername.php?id=' + id,
        type: 'post',
        dataType: 'json',
        data: {'newUsername': newUsername, 'password': password},
        success: function(data) {
            console.dir(data);
            if(data.status === 'succeeded') {
                Cookies.set('username', newUsername);
                $('#userDataWrapper').before('<p class="text-success">Successfully changed username to "' + newUsername + '"!</p>');
                $('#account').empty();
                $('#editPasswordBtn, #editUsernameBtn, #deleteUserBtn').removeClass('disabled');
                initUserData();
            }else if(data.reason === 'authentication failure'){
                $('#account').prepend('<p class="text-danger">Incorrect password provided!</p>');
            }else {
                $('#account').prepend('<p class="text-danger">There was an error updating your username.</p>');
            }
        },
        error: function(data) {
            console.dir(data);
            $('#account').prepend('<p class="text-danger">There was an error updating your username.</p>');
        }
    });
}

function initEditPassword() {
    var id = Cookies.get('logged_in');
    var formSkeleton = '<label for="currentPassword">Current Password: </label><br>'
                     + '<input type="password" id="currentPassword" /><br>'
                     + '<label for="newPassword">New Password: </label><br>'
                     + '<input type="password" id="newPassword" /><br>'
                     + '<label for="repeatNewPassword">Confirm New Password: </label><br>'
                     + '<input type="password" id="confirmNewPassword" /><br>'
                     + '<button class="btn btn-primary" onclick="editPassword(' + id + ')">Update</button>';
    $('#account').html(formSkeleton);
    $('#editPasswordBtn, #editUsernameBtn, #deleteUserBtn').addClass('disabled');
}

function editPassword(id) {
    var currentPassword = $('#currentPassword').val();
    var newPassword = $('#newPassword').val();
    var confirmNewPassword = $('#confirmNewPassword').val();
    
    if(newPassword === confirmNewPassword) {
        $.ajax({
            url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updatePassword.php?id=' + id,
            type: 'post',
            dataType: 'json',
            data: {'oldPassword': currentPassword, 'newPassword': newPassword},
            success: function(data) {
                console.dir(data);
                if(data.status === 'succeeded') {
                    $('#userDataWrapper').before('<p class="text-success">Successfully changed password!</p>');
                    $('#account').empty();
                    $('#editPasswordBtn, #editUsernameBtn, #deleteUserBtn').removeClass('disabled');
                }else if(data.reason === 'authentication failure'){
                    $('#account').prepend('<p class="text-danger">Incorrect password provided!</p>');
                    $('#currentPassword, #newPassword, #confirmNewPassword').val('');
                }else {
                    $('#account').prepend('<p class="text-danger">There was an error updating your password.</p>');
                    $('#currentPassword, #newPassword, #confirmNewPassword').val('');
                }
            },
            error: function(data) {
                console.dir(data);
                $('#account').prepend('<p class="text-danger">There was an error updating your password.</p>');
                $('#currentPassword, #newPassword, #confirmNewPassword').val('');
            }
        });
    }else {
        $('#account').prepend('<p class="text-danger">The passwords did not match.</p>');
        $('#currentPassword, #newPassword, #confirmNewPassword').val('');
    }
}

function initDeleteUser() {
    var id = Cookies.get('logged_in');
    var formSkeleton = '<p>Please enter your password to confirm the deletion of your account. <b>THIS CANNOT BE UNDONE!</b></p><br>'
                     + '<label for="password">Current Password: </label><br>'
                     + '<input type="password" id="password" placeholder="Password..." /><br>'
                     + '<button class="btn btn-danger" onclick="deleteUser(' + id + ')">Permanently Delete</button>';
    $('#account').html(formSkeleton);
}

function deleteUser(id) {
    var password = $('#password').val();
    
    $.ajax({
        url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/deleteUser.php?id=' + id,
        type: 'post',
        dataType: 'json',
        data: {'password': password},
        success: function(data) {
            console.dir(data);
            if(data.status === 'succeeded') {
                logout();
            }else if(data.reason === 'authentication failure'){
                $('#account').prepend('<p class="text-danger">Incorrect password provided!</p>');
            }else {
                $('#account').prepend('<p class="text-danger">There was an error deleting your user.</p>');
            }
        },
        error: function(data) {
            console.dir(data);
            $('#account').prepend('<p class="text-danger">There was an error deleting your user.</p>');
        }
    });
}