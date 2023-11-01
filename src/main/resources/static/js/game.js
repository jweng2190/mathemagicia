/* window.addEventListener('beforeunload', function () {
    if (eventSource != null) {
        eventSource.close();
    }
}); */
//firefox bug?? interrupted websocket

/* const countdownElement = document.getElementById('countdown_text');
const countdownContainer = document.getElementsByClassName('countdown-container')[0]; */
import { parseUserInputToLatex } from "./test_latex.js";
import { evaluateMathExpression } from "./test_latex.js";
import { animateXp } from "./game_xp.js";

let countdownElement;
let countdownContainer;
const loadingContainer = document.getElementsByClassName('loading-container')[0];
const content = document.getElementsByClassName('content')[0];
const backgroundDiv = document.getElementById('cd-bg-div');
const texts = ['READY', '3', '2', '1', 'GO!'];
const colors = ['rgb(0, 150, 255)', 'rgb(222, 49, 99)', 'rgb(255, 117, 24)', 'rgb(255, 191, 0)', 'rgb(15, 255, 80)'];
const badgeUrl = "https://mm-level-badges.s3.us-east-2.amazonaws.com";

let index = 0;

var loc = window.location, base_uri;
if (loc.protocol === "https:") {
    base_uri = "wss:";
} else {
    base_uri = "ws:";
}
base_uri += "//" + loc.host;

var stompClient = null;
var username;
getUsername().then((result) => {
    username = result;
});


//get the gameId
var gameUrl = window.location.href;
var gameIdIndex = gameUrl.lastIndexOf("/") + 1;
var gameId = gameUrl.substring(gameIdIndex);

let playerType;
getPlayerType(gameId).then((result) => {
    playerType = result;
    console.log(playerType);
});

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
let problemImage = document.getElementById("problem_image");

let player1Score = 0;
let player2Score = 0;

let player1Joined = false;
let player2Joined = false;

let playerAnswer = "";

/* let numD1 = 0;
let numD2 = 0;
getNumDisconnect(gameId).then((result) => {
    numD1 = result[0];
    numD2 = result[1];
}); */

//default value if not specified
let mins = 10;
getTimeLimit(gameId).then((timeString) => {
    mins = parseInt(timeString);
});

let countdown;

const leftWindow = document.getElementById('left-window');
const rightWindow = document.getElementById('right-window');

const player1ScoreElement = document.getElementById('player1-score');
const player2ScoreElement = document.getElementById('player2-score'); 

const gameContent = document.getElementsByClassName('game-page')[0];
let modalLink = document.getElementById("modal_link");

const rematchButton = document.getElementById('rematch');

const modalAnswer = document.getElementById('modal-answer');
const modalEnd = document.getElementById('modal-end');
//buttons for modal
const endClose = document.getElementById("end-close");
const amClose = document.getElementById("am-close");

const player1Badge = document.getElementById("player1_badge");
const player2Badge = document.getElementById("player2_badge");

let player1;
let player2;

playerInput.addEventListener('input', () => {
    setTimeout(() => {
        convertToLatex();
    }, 1000);
});

modalLink.addEventListener("click", () => {
    let modalA = new bootstrap.Modal(modalAnswer);
    modalA.show();
});

amClose.addEventListener("click", () => {
    let modalA = new bootstrap.Modal(modalAnswer);
    modalA.hide();
});

endClose.addEventListener("click", () => {
    let modalE = new bootstrap.Modal(modalEnd);
    modalE.hide();
});

rematchButton.addEventListener("click", rematch);

let inputMode = 'regular';

let regularButton = document.getElementById("regular_mode");
let scientificButton = document.getElementById("sci_mode");

regularButton.addEventListener("click", handleRegular);
scientificButton.addEventListener("click", handleScientific);

function handleRegular() {
    scientificButton.style.backgroundColor = "var(--bs-border-color-translucent)";
    inputMode = 'regular';
    regularButton.style.backgroundColor = "";
    console.log(inputMode);
}

function handleScientific() {
    regularButton.style.backgroundColor = "var(--bs-border-color-translucent)";
    inputMode = 'scientific';
    scientificButton.style.backgroundColor = "";
    console.log(inputMode);
}

function convertToLatex() {
    //const userInput = document.getElementById('user-input')
    const userInputValue = playerInput.value;
    const result = evaluateMathExpression(userInputValue);
    const latexCode = parseUserInputToLatex(userInputValue);
    console.log(latexCode);
    if(result === 'Error') {
        document.getElementById('latex-output').innerText = result;
    } else {
        const formattedLatex = `\\(` + latexCode + `\\)`
        document.getElementById('latex-output').innerText = formattedLatex;
        MathJax.Hub.Queue(['Typeset', MathJax.Hub, 'latex-output']);
    }
}

function startCDTimer() {
    countdownContainer.style.zIndex = '3';
    countdownContainer.style.visibility = 'visible';
    countdownElement.style.zIndex = '2';
    countdownElement.style.visibility = 'visible';
    setTimeout(fadeInAndOut, 1000);
}

function fadeInAndOut() {
    countdownElement.style.opacity = '1';
    countdownElement.innerHTML = texts[index];
    countdownElement.style.color = colors[index];
    setTimeout(() => {
        countdownElement.style.opacity = '0';
        index++;
        if (index < texts.length) {
            setTimeout(fadeInAndOut, 500);
        }
    }, 500);
}

function fadeOutBg() {
    backgroundDiv.style.opacity = '0';
    document.getElementsByClassName('content')[0].style.opacity = '1';
    backgroundDiv.style.zIndex = '-2';
    countdownContainer.style.zIndex = '-2';
}

async function createCountdown() {
    return new Promise(resolve => {
        let cdContainer = document.createElement('div');
        cdContainer.className = 'countdown-container';
        cdContainer.style.zIndex = '3';
        cdContainer.style.position = 'absolute';
        cdContainer.style.top = "50%";
        cdContainer.style.left = "50%";
        cdContainer.style.transform = "translate(-50%, -50%)";
        let cdElem = document.createElement('div');
        cdElem.className = 'countdown';
        cdElem.id = 'countdown';
        let cdText = document.createElement('p');
        cdText.id = 'countdown_text';
        cdText.style.fontWeight = '500';
        cdText.style.fontSize = '100px';
        cdText.style.opacity = '0';
        cdText.style.transition = 'opacity 0.5s';
        cdElem.appendChild(cdText);
        cdContainer.appendChild(cdElem);
        let leftWindow = document.getElementById("left-window");
        document.body.insertBefore(cdContainer, leftWindow);

        resolve([cdContainer, cdText]);
    });
}

async function getPlayerData() {
    const response = await fetch("/game/players?gameId=" + gameId);
    if(response.ok) {
        const data = await response.json();
        /* console.log("Player 1 Username: " + data[0]);
        console.log("Player 2 Username: " + data[1]); */
        return data;
    } else {
        //handle expection
        throw new Error("An error occurred. Unable to fetch player information.")
    }
}

async function getTimeLimit(gameId) {
    try {
        const response = await fetch("/game/time_limit", {
            method: "POST",
            headers: {
            "Content-Type": "text/plain",
            },
            body: gameId
        });
        if (!response.ok) {
            throw new Error("Something went wrong.");
        }
        return response.text();
    } catch (error) {
        console.error('Error:', error);
    }
}


function startGameTimer() {
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
    stompClient.send(`/ws/${message.type}`, {}, JSON.stringify(message));
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
            /* if(numD1 === 0 && numD2 === 0) {
                readyButton.style.visibility = "visible";
            } */
            startGame();
        }
        updateGame(message);
    },

    "game.ready": (message) => {
        updateGame(message);
    },

    "game.answer": (message) => {
        //TODO
    },
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

    "game.disconnect": (message) => {
        console.log(message.body);
        alert("Opponent disconnected! You win");
    }
}


function connect() {
    const socketConnect = new WebSocket(base_uri + "/connect", 'v10.stomp');
    console.log('Connecting to game');

    stompClient = Stomp.over(socketConnect);
    stompClient.connect({"gameId" : gameId}, function (frame) {
        console.log(frame);
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
    });
}

/* function reconnect() {
    const socketConnect = new WebSocket(base_uri + "/connect", 'v10.stomp');
    console.log('Reconnecting to game');

    stompClient = Stomp.over(socketConnect);
    stompClient.connect({"gameId" : gameId}, function (frame) {
        console.log(frame);
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
    });
} */

function connectStatus() {
    const socketStatus = new WebSocket(base_uri + "/status", 'v10.stomp');
    stompClient = Stomp.over(socketStatus);

    stompClient.connect({"gameId" : gameId}, function (frame) {
        stompClient.subscribe('/user/' + username + "/status", function (message) {
            var statusMsg = JSON.parse(message.body);
            console.log(statusMsg);

            let status = statusMsg.status;
            if(status === 'Disconnected') {
                alert("Opponent disconnected. You win!");
            }
        });
        let message = {
            gameId: gameId,
            playerUsername: username
        }
        stompClient.send("/ws/game.status", {}, JSON.stringify(message));
        connect();
    });
}

/* function readyUp() {
    readyButton.innerHTML = "Waiting for opponent...";
    const socketReady = new WebSocket(base_uri + "/ready", 'v10.stomp');
    stompClient = Stomp.over(socketReady);
    stompClient.connect({"gameId": gameId}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe(`/topic/game.ready`, function (message) {
            handleMessage(JSON.parse(message.body));
            handleGameStatus(message.body);
        });
        queueGame();
    });
}
 */

async function getPlayerType(gameId) {
    const response = await fetch("/game/type/" + gameId);
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const playerType = await response.text();
    return playerType;
}

/* async function getNumDisconnect(gameId) {
    const response = await fetch("/game/disconnect_num/" + gameId);
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const numDisconnect = await response.json();
    return numDisconnect;
} */


function joinGame() {
    sendMessage({
        type: "game.join",
        playerUsername: username,
        gameId: gameId
    });
}

/* function queueGame() {
    sendMessage({
        type: "game.ready",
        playerUsername: username,
        gameId: gameId
    });
} */


function updateGame(message) {
    let game = messageToGame(message);
    console.log(game);
}

async function startGame() {
    loadingContainer.children[0].src = '';
    loadingContainer.style.zIndex = '-3';
    
    try {
        let data = await getPlayerData();
        player1 = data[0];
        player2 = data[1];
    } catch(err) {
        throw new Error(err);
    }

    try{
        let b1Response = await fetch("/level?username=" + player1);
        let b2Response = await fetch("/level?username=" + player2);

        let b1Lvl = await b1Response.json();
        let b2Lvl = await b2Response.json();

        player1Badge.src = badgeUrl + "/b" + b1Lvl + ".png";
        player2Badge.src = badgeUrl + "/b" + b2Lvl + ".png";
    } catch(err) {
        console.log(err);
    }
    

    const result = await createCountdown();
    countdownContainer = result[0];
    countdownElement = result[1];

    setTimeout(fadeOutBg, 6000);
    startCDTimer();
    setTimeout(() => {
        //content.style.opacity = '1';
        startGameTimer();
        console.log("Game started!");
        //readyButton.style.visibility = "hidden";
        checkAnswer();
    }, 6500);   
}

/* function checkDisconnect() {
    stompClient.connect({}, function () {
        stompClient.send("/ws/setGameId", {}, JSON.stringify({ gameId: gameId }));
    }, function (error) {
        throw new Error(error);
    });
}
 */

var sendAnswer = function(event) {
    event.preventDefault();
    var playerAnswer = playerInput.value;
    playerInput.value = "";
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
    const socket = new WebSocket(base_uri + '/game_answer', 'v10.stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({"gameId": gameId}, function (frame) {
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
                //add back later
                //showScores();
                //setTimeout 5 seconds
                nextProblem();
            }
        });
        answerForm.style.visibility = "visible";
        problemNumberBox.innerHTML = "Problem " + (currentProblemIndex + 1);
        problemImage.src = currentProblem.image;
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
        problemImage.src = currentProblem.image;

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


async function showFinalScores() {
    let modal = new bootstrap.Modal(modalEnd);
    modal.show();

    const finalScore1 = document.getElementById("final_score1");
    const finalScore2 = document.getElementById("final_score2");

    finalScore1.innerHTML = player1Score;
    finalScore2.innerHTML = player2Score;

    /* getXpInfo(gameId, playerType).then((stats) => {
        console.log(stats);
        //get current stats
        const level = document.getElementById("player_level");
        const xpValue = document.getElementById("xp_value");
        const xpBar = document.getElementById("current_xp");
        let currentXp = stats[0][1];
        let xpLevelUp = stats[0][2];
        level.textContent = stats[0][0];
        xpValue.textContent = currentXp;
        const width = (currentXp / xpLevelUp) * 200;
        xpBar.style.width = width + "px";
        //xp animation
        animateXp(gameId, playerType, stats);
    }); */
    animateXp(gameId, playerType);
}


function endGame() {
    disableAnswer();
    stopTimer();
    let gameEndData = {
        gameId: gameId,
        playerUsername: username
    };
    gameEndData = JSON.stringify(gameEndData);
    postRequest("/game/game_end", gameEndData, "json").then((response) =>  {
        if(response === "Game Ended") {
            connectRematch();
        } else {
            throw new Error("Oops! Something went wrong.")
        }
    }).catch((error) => {
        throw new Error(error);
    });

    /* const socket = new WebSocket('/end');
    stompClient = Stomp.client(socket);
    stompClient.connect({"gameId": gameId}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe(`/topic/game.end`, function (message) {
            var isDone = (message.body === 'true');
            if(isDone === true) {
                connectRematch();
                showFinalScores();
            }
        });
        sendScores();
    }); */
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
    const socketRematch = new WebSocket(base_uri + '/rematch', 'v10.stomp');
    stompClient = Stomp.over(socketRematch);
    stompClient.connect({"gameId": gameId}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe(`/topic/game.rematch`, function (message) {
            handleRematchStatus(message.body);
        });
        showFinalScores();
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
    let parsedMessage = JSON.parse(message);
    let rematchStatus = parsedMessage[0];
    let newGameId = parsedMessage[1];

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
    connectStatus();
    //connect();
}

async function getUsername() {
    const response = await fetch('/username');
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const username = await response.text();
    return username;
}


//figure out bug
async function postRequest(url, data, type) {
    let contentType;
    if(type === "plain text") {
        contentType = "text/plain";
    } else if(type === "json") {
        contentType = "application/json";
    }

    const response = await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        mode: "same-origin", // no-cors, *cors, same-origin
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        credentials: "same-origin", // include, *same-origin, omit
        headers: {
            "Content-Type": contentType,
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "same-origin", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: data, // body data type must match "Content-Type" header
    });
    return response.text();
}
