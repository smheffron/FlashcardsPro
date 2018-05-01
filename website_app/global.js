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
//    console.log(setParam);
    if(setParam === -1) {
        $.ajax({
            url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/getSets.php',
            type: 'get',
            dataType: 'json',
            data: {'id': Cookies.get('logged_in')},
            success: function(data) {
//                console.dir(data);
                if(data.status === 'succeeded') {
                    $('#setsList').empty();
                    if(data.sets.length != 0) {
                        $.each(data.sets, function(index, set) {
                            $('#setsList').append('<div class="setWrapper" onclick="window.location=\'?set=' + set.setId + '\'"><p>' + set.setName + '</p></div><button class="btn btn-danger deleteCardBtn" onclick="deleteSet(' + set.setId + ')">Delete</button><br>');
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
//                console.dir(data);
                if(data.status === 'succeeded') {
                    cards = data.cards;
                    $('#setsList').attr('id', 'cardsList');
                    $('#newSetWrapper').attr('id', 'newCardWrapper');
                    $('#newCardWrapper > span').text('New Card: ');
                    $('#newSetBtn').attr('id', 'newCardBtn');
                    $('#newCardBtn').attr('onclick', 'initNewCard()');
                    $('#cardsList').empty();
                    $('#currentCard, .nextCard, .prevCard').remove();
                    $("#cardsList").click(function() {
                        if($(this).css("transform") == 'none'){
                            $(this).css("transform","rotateX(180deg)");
                            $('#cardsList p').css("transform","rotateX(180deg)");
                        } else {
                            $(this).css("transform","");
                            $('#cardsList p').css("transform","");
                        }
                    });
                    
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
                        $('#editCard').attr('onclick', 'initEditCard(0)');
                        $('#deleteCard').attr('onclick', 'deleteCard(' + cards[0].cardId + ')');
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

function initNewSet() {
    $('#newSetBtn').before($('<input>', {
        type: 'text',
        placeholder: 'New Set Name...',
        id: 'newSetName'
    }));
    $('#newSetBtn span').text('Create');
    $('#newSetBtn').removeClass('btn-default').addClass('btn-primary');
    $('#newSetBtn').attr('onclick', 'newSet()');
}

function initNewCard() {
    $('#newCardBtn').before($('<input>', {
        type: 'text',
        placeholder: 'Front Text...',
        id: 'newCardFront'
    }));
    $('#newCardBtn').before($('<input>', {
        type: 'text',
        placeholder: 'Back Text...',
        id: 'newCardBack'
    }));
    $('#newCardBtn span').text('Create');
    $('#newCardBtn').removeClass('btn-default').addClass('btn-primary');
    $('#newCardBtn').attr('onclick', 'newCard()');
}

function initEditCard(id) {
    $('#editDeleteWrapper').after($('<input>', {
        type: 'text',
        placeholder: 'Back Text...',
        id: 'existingCardBack',
        value: cards[id].backText
    }));
    $('#editDeleteWrapper').after($('<input>', {
        type: 'text',
        placeholder: 'Front Text...',
        id: 'existingCardFront',
        value: cards[id].frontText
    }));
    $('#existingCardFront').after('<br>');
    $('#existingCardBack').after('<br><button class="btn btn-primary editCardBtn" onclick="editCard(' + cards[id].cardId + ')">Update</button>');
    $('#editCard, .nextCard, .prevCard').addClass('disabled');
}

function editCard(id) {
    var cardFront = $('#existingCardFront').val();
    var cardBack = $('#existingCardBack').val();
    
    $.ajax({
        url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updateFlashcard.php?cardId=' + id,
        type: 'post',
        data: {'newFront': cardFront, 'newBack': cardBack},
        dataType: 'json',
        success: function(data) {
            if(data.status === 'succeeded') {
                $('#editCard, .nextCard, .prevCard').removeClass('disabled');
                $('#existingCardFront, #existingCardBack, .editCardBtn').remove();
                initSetsList();
            }else {
                $('#newCardWrapper').before('<p class="text-danger">Failed to edit flashcard</p>');
            }
        },
        error: function(data) {
            $('#newCardWrapper').before('<p class="text-danger">Failed to edit flashcard</p>');
            console.dir(data);
        }
    });
}

function deleteCard(id) {
    $.ajax({
        url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/deleteFlashcard.php',
        type: 'get',
        data: {'cardId': id},
        dataType: 'json',
        success: function(data) {
            if(data.status === 'succeeded') {
                initSetsList();
            }else {
                $('#newCardWrapper').before('<p class="text-danger">Failed to delete flashcard</p>');
            }
        },
        error: function(data) {
            $('#newCardWrapper').before('<p class="text-danger">Failed to delete flashcard</p>');
            console.dir(data);
        }
    });
}

function newCard() {
    var setParam = $.urlParam('set');
    var cardFront = $('#newCardFront').val();
    var cardBack = $('#newCardBack').val();
    
    if(setParam != -1) {
        $.ajax({
            url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/newFlashcard.php?setId=' + setParam,
            type: 'post',
            data: {'newCardFront': cardFront, 'newCardBack': cardBack},
            dataType: 'json',
            success: function(data) {
                if(data.status === 'succeeded') {
                    $('#newCardFront, #newCardBack').remove();
                    $('#newCardBtn span').html('&#xe081;');
                    $('#newCardBtn').attr('onclick', 'initNewCard()');
                    $('#newCardBtn').removeClass('btn-primary').addClass('btn-default');
                    initSetsList();
                }else {
                    $('#newCardWrapper').before('<p class="text-danger">Failed to create new set named "' + setName + '"</p>');
                }
            },
            error: function(data) {
                $('#newCardWrapper').before('<p class="text-danger">Failed to create new set named "' + setName + '"</p>');
                console.dir(data);
            }
        });
    }else {
        $('#newCardWrapper').before('<p class="text-danger">Failed to create new flashcard</p>');
    }
}

function newSet() {
    var setName = $('#newSetName').val();
    var id = Cookies.get('logged_in');
    
    $.ajax({
        url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/newCardSet.php?id=' + id,
        type: 'post',
        data: {'title': setName},
        dataType: 'json',
        success: function(data) {
            if(data.status === 'succeeded') {
                $('#newSetName').remove();
                $('#newSetBtn span').html('&#xe081;');
                $('#newSetBtn').attr('onclick', 'initNewSet()');
                $('#newSetBtn').removeClass('btn-primary').addClass('btn-default');
                initSetsList();
            }else {
                $('#newSetWrapper').before('<p class="text-danger">Failed to create new set named "' + setName + '"</p>');
            }
        },
        error: function(data) {
            $('#newSetWrapper').before('<p class="text-danger">Failed to create new set named "' + setName + '"</p>');
            console.dir(data);
        }
    });
}

function deleteSet(id) {
    $.ajax({
        url: 'http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/deleteCardSet.php',
        type: 'get',
        data: {'id': id},
        dataType: 'json',
        success: function(data) {
            if(data.status === 'succeeded') {
                initSetsList();
            }else {
                $('#newSetWrapper').before('<p class="text-danger">Failed to delete set</p>');
            }
        },
        error: function(data) {
            $('#newSetWrapper').before('<p class="text-danger">Failed to delete set</p>');
            console.dir(data);
        }
    });
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
    
    $('#editCard').attr('onclick', 'initEditCard(' + id + ')');
    $('#deleteCard').attr('onclick', 'deleteCard(' + cards[id].cardId + ')');
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
    
    $('#editCard').attr('onclick', 'initEditCard(' + id + ')');
    $('#deleteCard').attr('onclick', 'deleteCard(' + cards[id].cardId + ')');
}