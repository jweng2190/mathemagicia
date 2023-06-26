const url = "http://localhost:8080";
var stompClient = null;
var username;
getUsername().then((result) => {
    username = result;
});

var gameUrl = window.location.href;
var gameIdIndex = gameUrl.lastIndexOf("/") + 1;
var gameId = gameUrl.substring(gameIdIndex);

let currentProblem;
let currentProblemId;
let currentProblemIndex = 0;
let numProblems;
var problems;

let answerForm = document.getElementById("answer_form");

let readyButton = document.getElementById("ready");
//get answer from form
let playerInput = document.getElementsByTagName("input")[0];

let player1Score = 0;
let player2Score = 0;

let player1Joined = false;
let player2Joined = false;


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
        if(player1Joined && player2Joined) {
            readyButton.style.display = "block";
        }
        updateGame(message);
    },

    "game.ready": (message) => {
        updateGame(message);
    },

    "game.answer": (message) => {
        //TODO
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
    const socketConnect = new SockJS("/ws/connect");
    console.log('WebSocket connection established');

    stompClient = Stomp.over(socketConnect);
    stompClient.connect({}, function (frame) {
        console.log("Connected" + frame);
        stompClient.subscribe('/topic/game.state', function (message) {
            var messageObject = JSON.parse(message.body);
            player1Joined = messageObject.player1Joined;
            player2Joined = messageObject.player2Joined;
            handleMessage(messageObject);
            problems = messageObject.problemSet;
            numProblems = Object.keys(problems).length;
            currentProblem = problems[0];
            currentProblemId = problems[0].problemId;
            console.log("Current Problem: " + objectToProblem(currentProblem));
            console.log("Current ProblemId: " + currentProblemId);
            console.log("Current ProblemIndex: " + currentProblemIndex);
        });

        var isCreated;
        isCreatedByUser(gameId).then((result) => {
            console.log(result);
            isCreated = result;
        });

        setTimeout(() => {
            if(!isCreated) {
                joinGame();
            }
        }, 3000);
    });
};

function readyUp() {
    readyButton.innerHTML = "Waiting for opponent...";
    const socketReady = new SockJS("/ws/ready");
    stompClient = Stomp.over(socketReady);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe(`/topic/game.ready`, function (message) {
            handleMessage(JSON.parse(message.body));
            handleGameStatus(message.body);
        });
        queueGame();
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
}

function startGame() {
    console.log("Game started!");
    checkAnswer();
    /* document.getElementById("player1").innerHTML = game.player1;
    document.getElementById("player2").innerHTML = game.player2 || (game.winner ? '-' : 'Waiting for player 2...');
    document.getElementById("turn").innerHTML = game.turn;
    document.getElementById("winner").innerHTML = game.winner || '-'; */
}

//submitButton.addEventListener("submit", sendAnswer());

var sendAnswer = function(event) {
    event.preventDefault();
    var playerAnswer = playerInput.value;
    playerInput.value = "";
    //check
    if(stompClient !== null) {
        sendMessage({
            type: "game.answer",
            playerUsername: username,
            gameId: gameId,
            answer: playerAnswer,
            currentProblemId: currentProblemId,
            timestamp: Date.now()
        });
    }
}

function checkAnswer() {
    const socket = new SockJS('/ws/answer');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe(`/topic/game.answer`, function (message) {
            var message = JSON.parse(message.body);

            var originalScore1 = player1Score;
            var originalScore2 = player2Score;
            player1Score = message.score1;
            player2Score = message.score2;
            console.log("Player 1 Score: " + player1Score + "\nPlayer 2 Score: " + player2Score);

            if((player1Score - originalScore1 > 0) || (player2Score - originalScore2 > 0)) {
                nextProblem();
            }
        });
        answerForm.style.display = "block";
        answerForm.addEventListener("submit", sendAnswer);
    });
}

function nextProblem() {
    if(currentProblemIndex + 1 < numProblems) {
        currentProblemIndex++;
        currentProblem = problems[currentProblemIndex];
        currentProblemId = problems[currentProblemIndex].problemId;
        console.log("Current Problem: " + objectToProblem(currentProblem));
        console.log("Current ProblemId: " + currentProblemId);
        console.log("Current ProblemIndex: " + currentProblemIndex);
    } else if(currentProblemIndex + 1 == numProblems) {
        //end game
        alert("Game Ended!");
        //display score
        endGame();
    }
}

function endGame() {
    const socket = new SockJS('/ws/end');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe(`/topic/game.end`, function (message) {
            //Do nothing, game end
        });
        sendScores();
    });
}

function sendScores() {
    sendMessage({
        type: "game.end",
        gameId: gameId,
        playerUsername: username
    });
}


function handleGameStatus(message) {
    messageObject = JSON.parse(message);
    messageStatus = messageObject.status;
    if(messageStatus === "READY2") {
        readyButton.innerHTML = "READY!";
        setTimeout(startGame(), 2000);
    }

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
        player1Score: message.score1,
        player2Score: message.score2,
        player1Joined: message.player1Joined,
        player2Joined: message.player2Joined,
        gameStatus: message.gameStatus,
        problemSet: message.problemSet,
        winner: message.winner
    }
}

function objectToProblem(problemObject) {
    return {
        problemId: problemObject.problemId,
        contest: problemObject.contest,
        problemDescription: problemObject.problemDescription,
        difficulty: problemObject.difficulty,
        answer: problemObject.answer,
        solution: problemObject.solution
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
