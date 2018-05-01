$.urlParam = function(name){
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if(results == null) {return -1;}else {return results[1];}
}

var cards = [];

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
    var setParam = $.urlParam('set');
    console.log(setParam);
    if(setParam === -1) {
        $.ajax({
            url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/getSets.php',
            type: 'get',
            dataType: 'json',
            data: {'id': Cookies.get('logged_in')},
            success: function(data) {
                console.dir(data);
                if(data.status === 'succeeded') {
                    if(data.sets.length != 0) {
                        $.each(data.sets, function(index, set) {
                            $('#setsList').append('<div class="setWrapper" onclick="window.location=\'?set=' + set.setId + '\'"><p>' + set.setName + '</p></div>');
                        });
                    }else {
                        $('#setsList').append('<p>You have no sets!</p>');
                    }
                }
            }
        });
    }else {
        $.ajax({
            url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/getFlashcards.php',
            type: 'get',
            dataType: 'json',
            data: {'setId': setParam},
            success: function(data) {
                console.dir(data);
                if(data.status === 'succeeded') {
                    cards = data.cards;
                    $('#setsList').attr('id', 'cardsList');
                    if(cards.length != 0) {
                        $('#title').text('Flashcards');
                        $('#cardsList').attr('onclick', 'flipCard()');
                        $('#cardsList').append('<p>' + cards[0].frontText + '</p>');
                        $('#currentCard').val(0);
                        $('#cardsList').before('<input id="currentCard" type="hidden" value="0" /><div class="btn btn-default prevCard" onclick="prevCard()">&#9664;</div>');
                        $('#cardsList').after('<div class="btn btn-default nextCard" onclick="nextCard()">&#9654;</div>');
                        $.each(data.cards, function(index, card) {
                            cards[index].selected = "front";
                        });
                    }else {
                        $('#cardsList').append('<p>You have no flashcards in this set!</p>');
                    }
                }
            },
            error: function(data) {
                console.dir(data);
            }
        });
    }
}

function flipCard() {
    var id = $('#currentCard').val();
    
    if(cards[id].selected === 'front') {
        $('#cardsList p').text(cards[id].backText);
        cards[id].selected = 'back';
    }else {
        $('#cardsList p').text(cards[id].frontText);
        cards[id].selected = 'front';
    }
}

function nextCard() {
    var id = $('#currentCard').val();
    if(++id >= cards.length) {
        id = 0;
    }
    $('#currentCard').val(id);
    if(cards[id].selected === 'front') {
        $('#cardsList p').text(cards[id].frontText);
    }else {
        $('#cardsList p').text(cards[id].backText);
    }
}

function prevCard() {
    var id = $('#currentCard').val();
    if(--id < 0) {
        id = cards.length - 1;
    }
    $('#currentCard').val(id);
    if(cards[id].selected === 'front') {
        $('#cardsList p').text(cards[id].frontText);
    }else {
        $('#cardsList p').text(cards[id].backText);
    }
}