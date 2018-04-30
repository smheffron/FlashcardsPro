function verifyLogin() {
    if(Cookies.get('logged_in') == null && window.location.pathname != '/index.html') {
        window.location = '/index.html';
    }
}