/* window.addEventListener('beforeunload', function () {
    if (eventSource != null) {
        eventSource.close();
    }
}); */
//firefox bug?? interrupted websocket


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
let playerInput = document.getElementById("player_answer");

let player1ScoreField = document.getElementById("player1score_field");
let player2ScoreField = document.getElementById("player2score_field");

let problemNumberBox = document.getElementById("problem_number");
let problemDescriptionBox = document.getElementById("problem_description");
let problemImage = document.getElementById("problem_image");

let player1Score = 0;
let player2Score = 0;

let player1Joined = false;
let player2Joined = false;


const mins = 10;
let countdown;

const leftWindow = document.getElementById('left-window');
const rightWindow = document.getElementById('right-window');

const player1ScoreElement = document.getElementById('player1-score');
const player2ScoreElement = document.getElementById('player2-score'); 

const gameContent = document.getElementsByClassName('game-page')[0];

var myModal = new bootstrap.Modal(document.getElementById('modal-end'));


function startTimer() {
    let timer = document.getElementById('timer_p');
    timer.style.visibility = "visible";

    const now = new Date().getTime();
    const deadline = mins * 60 * 1000 + now;

    countdown = setInterval(() => {
        var currentTime = new Date().getTime();
        var distance = deadline - currentTime;
        var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        var seconds = Math.floor((distance % (1000 * 60)) / 1000);
        if (minutes <= 0 && seconds <= 0) {
            stopTimer();
            alert('Time is up!');
            endGame();
        }

        const formattedTime = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        document.getElementById('timer_p').innerHTML = formattedTime;
    }, 500);
}


function stopTimer() {
    clearInterval(countdown); // Stop the timer
    //updateTimer(); // Update the timer display
}


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
            readyButton.style.visibility = "visible";
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
    const socketConnect = new SockJS("/connect");
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
        
        joinGame();
        /* var isCreated;
        isCreatedByUser(gameId).then((result) => {
            console.log(result);
            isCreated = result;
        }); */

        /* setTimeout(() => {
            if(!isCreated) {
                joinGame();
            }
        }, 3000); */
    });
};

function readyUp() {
    readyButton.innerHTML = "Waiting for opponent...";
    const socketReady = new SockJS("/ready");
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
        playerUsername: username,
        gameId: gameId
    });
}

function queueGame() {
    sendMessage({
        type: "game.ready",
        playerUsername: username,
        gameId: gameId
    });
}


function updateGame(message) {
    game = messageToGame(message);
    console.log(game);
}

function startGame() {
    startTimer();
    console.log("Game started!");
    readyButton.style.visibility = "hidden";
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
    const socket = new SockJS('/answer');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe(`/topic/game.answer`, function (message) {
            var message = JSON.parse(message.body);

            var originalScore1 = player1Score;
            var originalScore2 = player2Score;
            player1Score = message.score1;
            player2Score = message.score2;

            player1ScoreField.innerHTML = player1Score;
            player2ScoreField.innerHTML = player2Score;

            console.log("Player 1 Score: " + player1Score + "\nPlayer 2 Score: " + player2Score);

            if ((player1Score - originalScore1 > 0) || (player2Score - originalScore2 > 0)) {
                showScores();
                setTimeout(() => {

                    nextProblem();
                }, 5000);
            }
        });
        answerForm.style.visibility = "visible";
        problemNumberBox.innerHTML = "Problem " + (currentProblemIndex + 1);
        problemDescriptionBox.innerHTML = currentProblem.problemDescription;
        if(currentProblem.image == null) {
            problemImage.removeAttribute("src");
            problemImage.style.flex = 0;
        } else {
            problemImage.src = 'data:image/jpeg;base64,' + currentProblem.image;
        }
        answerForm.addEventListener("submit", sendAnswer);
    });
}

function nextProblem() {
    //send game page back to front
    gameContent.style.zIndex = 1;

    if(currentProblemIndex + 1 < numProblems) {
        currentProblemIndex++;
        currentProblem = problems[currentProblemIndex];
        currentProblemId = problems[currentProblemIndex].problemId;

        problemNumberBox.innerHTML = "Problem " + (currentProblemIndex + 1);
        problemDescriptionBox.innerHTML = currentProblem.problemDescription;
        if(currentProblem.image == null) {
            problemImage.removeAttribute("src");
            problemImage.style.flex = 0;
        } else {
            problemImage.src = 'data:image/jpeg;base64,' + currentProblem.image;
            //show image if there is one
            problemImage.style.flex = 25;
        }

        console.log("Current Problem: " + objectToProblem(currentProblem));
        console.log("Current ProblemId: " + currentProblemId);
        console.log("Current ProblemIndex: " + currentProblemIndex);
    } else if(currentProblemIndex + 1 == numProblems) {
        //end game
        //alert("Game Ended!");
        //display score
        endGame();
    }
}

function disableAnswer() {
    let formDiv = answerForm.children[0];
    let divChildren = formDiv.children;
    let answerInput = divChildren[0];
    let answerButton = divChildren[1];

    answerInput.readOnly = true;
    answerButton.disabled = true;
}

function showScores() {
    player1ScoreElement.style.animation = "";
    player2ScoreElement.style.animation = "";

    //send to back
    gameContent.style.zIndex = -1;
    leftWindow.style.animation = 'slide-in-left 1.5s forwards';
    rightWindow.style.animation = 'slide-in-right 1.5s forwards';
    
    setTimeout(() => {
        playScoreAnimation(true);
        playScoreAnimation(false);
    }, 1500);

    setTimeout(() => {
        leftWindow.style.animation = 'slide-out-left 1.5s forwards';
        rightWindow.style.animation = 'slide-out-right 1.5s forwards';
    }, 3000);
}

function playScoreAnimation(player) {
    if (player === true) {
        player1ScoreElement.style.animation = 'score-animation 1.5s';
        setTimeout(() => {
            player1ScoreElement.innerText = player1Score;
        }, 300);  
    } else if(player === false) {
        player2ScoreElement.style.animation = 'score-animation 1.5s';
        setTimeout(() => {
            player2ScoreElement.innerText = player2Score;
        }, 300);  
    }
}


function showFinalScores() {
    //var myModal = new bootstrap.Modal(document.getElementById('modal-end'));
    myModal.show();
}

function closeModal() {
    //var myModal = new bootstrap.Modal(document.getElementById('modal-end'));
    myModal.hide();
}

function endGame() {
    disableAnswer();
    stopTimer();
    const socket = new SockJS('/end');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe(`/topic/game.end`, function (message) {
            var isDone = (message.body === 'true');
            if(isDone === true) {
                connectRematch();
                showFinalScores();
            }
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

function sendRematch(rematchStatus) {
    sendMessage({
        type: "game.rematch",
        gameId: gameId,
        playerUsername: username,
        accepted: rematchStatus,
        currentTime: Date.now()
    });
}

function rematch() {
    sendRematch(true);
}


function returnHome() {
    sendRematch(false);   
}

function connectRematch() {
    const socketRematch = new SockJS('/rematch');
    stompClient = Stomp.over(socketRematch);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe(`/topic/game.rematch`, function (message) {
            handleRematchStatus(message.body);
        });
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

function handleRematchStatus(message) {
    parsedMessage = JSON.parse(message);
    rematchStatus = parsedMessage[0];
    newGameId = parsedMessage[1];

    if(rematchStatus === "REMATCH2") {
        console.log("REMATCH SUCCESS");
        window.location.href = "/game/" + newGameId;
    } else if(rematchStatus === "REMATCH1") {
        console.log("WAITING FOR REMATCH");
    } else {
        console.log("NO REMATCH");
    }
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
