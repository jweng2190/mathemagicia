import { evaluateMathExpression } from "./test_latex.js";
import { animateXp } from "./game_xp.js";
import { getSideBar } from "./sidebar.js";
import { getBaseUri } from "./get_base_uri.js";

var username;
var gameId;
var mins = 10;
//problem stuff
var currentProblem;
var currentProblemId;
var currentProblemIndex = 0;
var numProblems;
var problemList;
var statusList;

var computerLevel;
let botTimes;
let botFinalScore;

setUpGame();

async function setUpGame() {
    username = await getUsername();
    
    gameId = sessionStorage.getItem("gameId");
    computerLevel = sessionStorage.getItem("computerLevel");

    let minsStr = await getTimeLimit(gameId);
    mins = parseInt(minsStr, 10);
    problemList = await getProblemList(gameId);
    numProblems = Object.keys(problemList).length;
    currentProblem = problemList[0];
    currentProblemId = problemList[0].problemId;
    statusList = new Array(numProblems).fill(-1);

    //debugging
    /* console.log(problemList);
    console.log("Current ProblemId: " + currentProblemId);
    console.log("Current ProblemIndex: " + currentProblemIndex); */

    getBotData();
    startGame();
}

/* async function sendDisconnect() {
    try {
        const reqBody = JSON.stringify({
            "gameId": gameId, "username": username
        });
        const response = await fetch("/game/disconnect", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: reqBody,
            keepalive: true
        });
    } catch(e) {
        console.log(e);
    }
} */

let countdownElement;
let countdownContainer;
const loadingContainer = document.getElementsByClassName('loading-container')[0];
const content = document.getElementsByClassName('content')[0];
const backgroundDiv = document.getElementById('cd-bg-div');
const texts = ['READY', '3', '2', '1', 'GO!'];
const colors = ['rgb(0, 150, 255)', 'rgb(222, 49, 99)', 'rgb(255, 117, 24)', 'rgb(255, 191, 0)', 'rgb(15, 255, 80)'];
const badgeUrl = "https://mm-level-badges.s3.us-east-2.amazonaws.com";

getSideBar("game");
let index = 0;

const base_uri = getBaseUri();

let answerForm = document.getElementById("answer_form");
let answerSubmit = document.getElementById("answer_submit");

//get answer from form
let playerInput = document.getElementById("player_answer");

let player1ScoreField = document.getElementById("player1score_field");
let player2ScoreField = document.getElementById("player2score_field");

let problemNumberBox = document.getElementById("problem_number");
let problemImage = document.getElementById("problem_image");

let player1Score = 0;
let player2Score = 0;

var gameWinner = null;
let countdown;

async function getBotData() {
    try {
        let message = {
            "gameId": gameId,
            "computerLevel": computerLevel
        };
        message = JSON.stringify(message);
        const response = await fetch("/game/bot_stats", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: message,
        });
        const botInfo = await response.json();
        
        botFinalScore = botInfo[0];
        botTimes = botInfo[1];
        console.log(botFinalScore);
        console.log(botTimes);
    } catch(e) {
        console.error(e);
    }
}

const gameContent = document.getElementsByClassName('game-page')[0];
let modalLink = document.getElementById("modal_link");

const retHome = document.getElementById("return_home");

const modalAnswer = document.getElementById('modal-answer');
const modalEnd = document.getElementById('modal-end');

var modalA = new bootstrap.Modal(modalAnswer, {backdrop: 'static', keyboard: false});
var modalE = new bootstrap.Modal(modalEnd, {backdrop: 'static', keyboard: false});

const player1Badge = document.getElementById("player1_badge");
const player2Badge = document.getElementById("player2_badge");

let navFirst, navPrev, pCurrProb, navNext, navLast;
let statusImg = document.getElementById("status_img");
let statusSpan = document.getElementById("status_span");

let player1;

playerInput.addEventListener('input', () => {
    setTimeout(() => {
        convertToLatex();
    }, 1000);
});

modalLink.addEventListener("click", () => {
    modalA.show();
});

retHome.addEventListener("click", () => {
    window.location.href = "/home";
});

function convertToLatex() {
    const userInputValue = playerInput.value;
    const result = evaluateMathExpression(userInputValue);
    if(result === 'Error') {
        document.getElementById('latex-output').innerText = result;
    } else {
        const latexCode = parseToLatex(userInputValue);
        console.log(latexCode);
        const formattedLatex = `\\(` + latexCode + `\\)`
        document.getElementById('latex-output').innerText = formattedLatex;
        MathJax.Hub.Queue(['Typeset', MathJax.Hub, 'latex-output']);
    }
}

function parseToLatex(input) {
    const nodeInput = math.parse(input);
    const latexCode = nodeInput.toTex();
    return latexCode;
}

async function startCDTimer() {
    return new Promise(resolve => {
        countdownContainer.style.zIndex = '3';
        countdownContainer.style.visibility = 'visible';
        countdownElement.style.zIndex = '2';
        countdownElement.style.visibility = 'visible';
        fadeInAndOut().then(() => {
            resolve("Countdown complete");
        });
    });
}

async function fadeInAndOut() {
    return new Promise(resolve => {
        countdownElement.style.opacity = '1';
        countdownElement.innerHTML = texts[index];
        countdownElement.style.color = colors[index];
        setTimeout(() => {
            countdownElement.style.opacity = '0';
            index++;
            if (index < texts.length) {
                setTimeout(() => {
                    fadeInAndOut().then(resolve);
                }, 500);
            } else {
                resolve();
            }
        }, 500);
    });
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

/* async function getPlayerData() {
    const response = await fetch("/game/players?gameId=" + gameId);
    if(response.ok) {
        const data = await response.json();
        return data;
    } else {
        throw new Error("An error occurred. Unable to fetch player information.")
    }
} */

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

async function getProblemList(gameId) {
    try {
        const response = await fetch("/game/problem_list", {
            method: "POST",
            headers: {
            "Content-Type": "text/plain",
            },
            body: gameId
        });
        if (!response.ok) {
            throw new Error("Something went wrong.");
        }
        return response.json();
    } catch (error) {
        console.error('Error:', error);
    }
}


function startGameTimer() {
    let timer = document.getElementById('timer_p');
    timer.style.visibility = "visible";

    const now = new Date().getTime();
    const deadline = mins * 60 * 1000 + now;
    //const deadline = mins * 60 * 1000 + now;

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


const sendMessage = (message, client) => {
    client.send(`/ws/${message.type}`, {}, JSON.stringify(message));
}

async function animateBotScore(client) {
    let botMsg = {
        type: "bot",
        gameId: gameId
    };

    for(let i = 0; i < botTimes.length; i++) {
        await delay(botTimes[i] * 1000);
        client.send(`/ws/computer`, {}, JSON.stringify(botMsg));
        doScoreAnim();
    }
}

function delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function doScoreAnim() {
    ++player2Score;
    player2ScoreField.classList.add("animated-score");
    setTimeout(() => {
        player2ScoreField.innerHTML = player2Score;
    }, 300);
    setTimeout(() => {
        if(player2ScoreField.classList.contains("animated-score")) {
            player2ScoreField.classList.remove('animated-score');
        }
    });
}

function handleProbNav() {
    navFirst = document.getElementById("nav_first");
    navPrev = document.getElementById("nav_prev");
    pCurrProb = document.getElementById("curr_prob");
    navNext = document.getElementById("nav_next");
    navLast = document.getElementById("nav_last");

    navFirst.addEventListener("click", goFirst);
    navPrev.addEventListener("click", goPrev);
    navNext.addEventListener("click", goNext);
    navLast.addEventListener("click", goLast);

    pCurrProb.innerHTML = (currentProblemIndex + 1) + "/" + numProblems;
}

function goFirst() {
    currentProblemIndex = 0;
    displayProb();
    showProbStatus(statusList[currentProblemIndex]);
}

function goPrev() {
    if(currentProblemIndex >= 1) {
        --currentProblemIndex;
        displayProb();
    }
    showProbStatus(statusList[currentProblemIndex]);
}

function goNext() {
    if(currentProblemIndex + 1 < numProblems) {
        ++currentProblemIndex;
        displayProb();
    }
    showProbStatus(statusList[currentProblemIndex]);
}

function goLast() {
    currentProblemIndex = numProblems - 1;
    displayProb();
    showProbStatus(statusList[currentProblemIndex]);
}

function displayProb() {
    currentProblem = problemList[currentProblemIndex];
    currentProblemId = problemList[currentProblemIndex].problemId;

    problemNumberBox.innerHTML = "Problem " + (currentProblemIndex + 1);
    problemImage.src = currentProblem.image;
    pCurrProb.innerHTML = (currentProblemIndex + 1) + "/" + numProblems;
}

function showProbStatus(probStatus) {
    switch(probStatus) {
        case -1:
            //not attempted
            statusImg.style.display = "none";
            statusSpan.style.display = "";
            break;
        case 0:
            statusSpan.style.display = "none";
            statusImg.src = "/img/incorrect.png";
            statusImg.style.display = "";
            break;
        case 1:
            statusSpan.style.display = "none";
            statusImg.src = "/img/correct.png";
            statusImg.style.display = "";
            break;
    }
}

async function getPlayerType(gameId) {
    const response = await fetch("/game/type/" + gameId);
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const playerType = await response.json();
    return playerType;
}


async function startGame() {
    loadingContainer.children[0].src = '';
    loadingContainer.style.zIndex = '-3';
    
    try {
        player1 = username;

        let p1Username = document.getElementById("player1_username");
        let p2Username = document.getElementById("player2_username");

        p1Username.innerHTML = player1;
        p2Username.innerHTML = "Bot Lvl " + computerLevel;
    } catch(err) {
        throw new Error(err);
    }

    try{
        let b1Response = await fetch("/level?username=" + player1);

        let b1Lvl = await b1Response.json();

        player1Badge.src = badgeUrl + "/b" + b1Lvl + ".png";
        player2Badge.src = "/img/bot.png";
    } catch(err) {
        console.log(err);
    }
    
    const result = await createCountdown();
    countdownContainer = result[0];
    countdownElement = result[1];

    startCDTimer().then(() => {
        startGameTimer();
        //animateBotScore();
        checkAnswer();
        fadeOutBg();
        handleProbNav();
    });
}

function sendIndivAnswer(client) {
    let playerAnswer = playerInput.value;
    playerInput.value = "";

    sendMessage({
        type: "computer",
        playerUsername: username,
        gameId: gameId,
        answer: playerAnswer,
        currentProblemIndex: currentProblemIndex,
        currentProblemId: currentProblemId,
        timestamp: Date.now()
    }, client);
}

function checkAnswer() {
    const socket = new WebSocket(base_uri + '/bot', 'v10.stomp');
    let stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe('/user/' + username + '/bot', function (message) {
            let msg = JSON.parse(message.body);

            gameWinner = msg[2];
            let type = msg[3];
            if(type === 1) {
                player1Score = msg[0];
                let answerStatus = msg[1];
                player1ScoreField.innerHTML = player1Score;
                console.log("Player 1 Score: " + player1Score);

                statusList[currentProblemIndex] = answerStatus;
                showProbStatus(statusList[currentProblemIndex]);
                if(gameWinner != null) {
                    endGame();
                }
            } else {
                if(gameWinner != null) {
                    endGame();
                }
            }

            /* if ((player1Score - originalScore1 > 0) || (player2Score - originalScore2 > 0)) {
                //add back later
                //showScores();
                //setTimeout 5 seconds
                nextProblem();
            } */
        });
        answerForm.style.visibility = "visible";
        problemNumberBox.innerHTML = "Problem " + (currentProblemIndex + 1);
        problemImage.src = currentProblem.image;
        answerForm.addEventListener("submit", function(event) {
            event.preventDefault();
            let client = stompClient;
            sendIndivAnswer(client);
        });

        answerSubmit.addEventListener("click", function(event) {
            event.preventDefault();
            let client = stompClient;
            sendIndivAnswer(client);
        });

        animateBotScore(stompClient);
    });
}


function disableAnswer() {
    let formDiv = answerForm.children[0];
    let divChildren = formDiv.children;
    let answerInput = divChildren[0];
    let answerButton = divChildren[1];

    answerInput.readOnly = true;
    answerButton.disabled = true;
}


async function showFinalScores() {
    modalE.show();

    const finalScore1 = document.getElementById("final_score1");
    const finalScore2 = document.getElementById("final_score2");

    if(username === gameWinner) {
        document.getElementById("game-result").innerHTML = "You Won!";
        document.getElementById("go-header").style.backgroundColor = "rgb(255, 172, 28)";
    } else {
        document.getElementById("game-result").innerHTML = "Game Over!";
        document.getElementById("go-header").style.backgroundColor = "rgb(165, 157, 144)";
    }

    finalScore1.innerHTML = player1Score;
    finalScore2.innerHTML = player2Score;
}


async function endGame() {
    disableAnswer();
    stopTimer();
    gameContent.style.zIndex = 3;
    showFinalScores();
    let gameEndData = {
        "gameId": gameId,
        "playerUsername": username
    };
    gameEndData = JSON.stringify(gameEndData);
    
    try {
        const response = await fetch("/game/game_end", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: gameEndData
        });
        const xpWinnerData = await response.json();
        const xpData = xpWinnerData.slice(0, 7);
        gameWinner = xpWinnerData[7];
        animateXp(xpData);
        showFinalScores();
    } catch(error) {
        throw new Error(error);
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

async function getUsername() {
    const response = await fetch('/username');
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const username = await response.text();
    return username;
}
