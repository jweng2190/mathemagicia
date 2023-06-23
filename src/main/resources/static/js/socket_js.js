const url = "http://localhost:8080";
var stompClient = null;
var username;
getUsername().then((result) => {
    username = result;
});

var gameUrl = window.location.href;
var gameIdIndex = gameUrl.lastIndexOf("/") + 1;
var gameId = gameUrl.substring(gameIdIndex);

const sendMessage = (message) => {
    stompClient.send(`/app/${message.type}`, {}, JSON.stringify(message));
}

const handleMessage = (message) => {
    if (messagesTypes[message.type])
        messagesTypes[message.type](message);
}

const messagesTypes = {
    "game.join": (message) => {
        updateGame(message);
    },
    /* "game.gameOver": (message) => {
        updateGame(message);
        if (message.gameState === 'TIE') alert("Game over! It's a tie!");
        else showWinner(message.winner);
    }, */
    "game.joined": (message) => {
        updateGame(message);
    },

    "game.ready": (message) => {
        updateGame(message);
    }
    /* "game.move": (message) => {
        updateGame(message);
    },
    "game.left": (message) => {
        updateGame(message);
        if (message.winner) showWinner(message.winner);
    },
    "error": (message) => {
        toastr.error(message.content);
    } */
}


function connect() {
    const socket = new SockJS('/ws/connect');

    console.log('WebSocket connection established');

    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected" + frame);
        stompClient.subscribe('/topic/game.state', function (message) {
            handleMessage(JSON.parse(message.body));
        });

        var isCreated;
        isCreatedByUser(gameId).then((result) => {
            console.log(result);
            isCreated = result;
        });

        setTimeout(() => {
        if(!isCreated) {
            joinGame();
        }}, 3000);  
    });
};

function readyUp() {
    readyButton = document.getElementById("ready");
    readyButton.innerHTML = "Waiting for opponent...";
    const socket = new SockJS('/ws/ready');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe(`/topic/game.ready`, function (message) {
            //still need to do stuff with game status
            handleMessage(JSON.parse(message.body));
            handleGameStatus(message);
        });
        setTimeout(queueGame(), 3000);
    });
}


async function isCreatedByUser(gameId) {  
    const response = await fetch("/game/created/" + gameId);
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const isCreated = await response.text();
    return (isCreated === "true");
}


function joinGame() {
    sendMessage({
        type: "game.join",
        playerUsername: username
    });
}

function queueGame() {
    sendMessage({
        type: "game.ready",
        playerUsername: username
    });
}


function updateGame(message) {
    game = messageToGame(message);
    console.log(game);
    /* document.getElementById("player1").innerHTML = game.player1;
    document.getElementById("player2").innerHTML = game.player2 || (game.winner ? '-' : 'Waiting for player 2...');
    document.getElementById("turn").innerHTML = game.turn;
    document.getElementById("winner").innerHTML = game.winner || '-'; */
}

function handleGameStatus(message) {
    messageStatus = message.gameStatus;
    console.log(messageStatus);
}

function messageToGame(message) {
    /*message.setGameId(game.getGameId());
        message.setPlayer1(game.getPlayer1());
        message.setPlayer2(game.getPlayer2());
        message.setGameStatus(game.getStatus());
        message.setWinner(game.getWinner()); */
    return {
        gameId: message.gameId,
        player1: message.player1,
        player2: message.player2,
        gameStatus: message.gameStatus,
        winner: message.winner
    }
}

window.onload = function() {
    connect();
}

async function getUsername() {
    const response = await fetch(url + '/username');
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const username = await response.text();
    return username;
}


/* function connectToRandom() {
    getUsername().then(username => {
        $.ajax({
            url: url + "/game/connect/random",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "username": username
            }),
            success: function (data) {
                gameId = data.gameId;
                connectToSocket(gameId);
                alert("Congrats, you're playing with: " + data.player1.username);
                window.location.replace("/game");
            },
            error: function (error) {
                console.log(error);
            }
        })
    })   
}

function connectToSpecificGame() {
    let gameId = document.getElementById("game_id").value;
    if (gameId == null || gameId === '') {
        alert("Please enter game id");
    }
    getUsername().then(username => {
        $.ajax({
            url: url + "/game/connect",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "player": {
                    "username": username
                },
                "gameId": gameId
            }),
            success: function (data) {
                gameId = data.gameId;
                connectToSocket(gameId);
                alert("Congrats, you're playing with: " + data.player1.username);
                window.location.replace(url + "/game?gameId=" + gameId);
            },
            error: function (error) {
                console.log(error);
            }
        })
    })
} */