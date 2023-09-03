import { evaluateMathExpression } from "./test_latex.js";
import { postRequest } from "./post_request.js";

let countdownElement;
let countdownContainer;
const loadingContainer = document.getElementsByClassName('loading-container')[0];
const content = document.getElementsByClassName('content')[0];
const backgroundDiv = document.getElementById('cd-bg-div');
const texts = ['READY', '3', '2', '1', 'GO!'];
const colors = ['rgb(0, 150, 255)', 'rgb(222, 49, 99)', 'rgb(255, 117, 24)', 'rgb(255, 191, 0)', 'rgb(15, 255, 80)']
let index = 0;
var stompClient;

//IMPORTANT! MODIFY LATER
const numProblems = 3;

let username;
getUsername().then((result) => {
    username = result;
    player1Name.innerHTML = username;
});

const gameId = sessionStorage.getItem("gameId");
const computerLevel = sessionStorage.getItem("computerLevel");

let answerForm = document.getElementById("answer_form");
let playerInput = document.getElementById("player_answer");

let playerScore = 0;
let botScore = 0;

let player1ScoreField = document.getElementById("player1score_field");
let player2ScoreField = document.getElementById("player2score_field");

let player1Name = document.getElementById("player1_username");
let player2Name = document.getElementById("player2_username");

player2Name.innerHTML = "Bot Lvl " + computerLevel;

let problemNumberBox = document.getElementById("problem_number");
let problemImage = document.getElementById("problem_image");

//default value if not specified
let mins = 10;
getTimeLimit(gameId).then((timeString) => {
    mins = parseInt(timeString);
});

let problems;
let currentProblemId;
let currentProblemIndex = 0;
let currentProblem;
try {
    problems = await postRequest("/game/problem_list", gameId, "plain text", "json");
    console.log(problems);
    currentProblemId = problems[currentProblemIndex].problemId;
    currentProblem = problems[currentProblemIndex];
} catch(e) {
    console.error(e);
}

let botInfo;
let botFinalScore;
try {
    let message = {
        "gameId": gameId,
        "computerLevel": computerLevel
    };
    message = JSON.stringify(message);
    botInfo = await postRequest("/game/bot_stats", message, "json", "json");
    
    botFinalScore = botInfo.shift();
    botInfo = botInfo[0];
    console.log(botFinalScore);
    console.log(botInfo);
} catch(e) {
    console.error(e);
}

let inputMode = 'regular';

let regularButton = document.getElementById("regular_mode");
let scientificButton = document.getElementById("sci_mode");

regularButton.addEventListener("click", handleRegular);
scientificButton.addEventListener("click", handleScientific);

let countdown;
const gameContent = document.getElementsByClassName('game-page')[0];
const myModal = document.getElementById("modal-end");

playerInput.addEventListener('input', () => {
    setTimeout(() => {
        convertToLatex();
    }, 1000);
});

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
            //stopTimer();
            alert('Time is up!');
            //endGame();
        }

        const formattedTime = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        document.getElementById('timer_p').innerHTML = formattedTime;
    }, 500);
}

function stopTimer() {
    clearInterval(countdown); // Stop the timer
    //updateTimer(); // Update the timer display
}

async function startGame() {
    loadingContainer.children[0].src = '';
    loadingContainer.style.zIndex = '-3';
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
        connectAnswer();
        animateBotScore();
    }, 6500);
}

function connectAnswer() {
    const socketBot = new SockJS("/bot");
    stompClient = Stomp.over(socketBot);
    stompClient.connect({}, function (frame) {
        console.log(frame);
        stompClient.subscribe('/user/' + username + "/bot", function (message) {
            let score = JSON.parse(message.body);
            if(score > playerScore) {
                playerScore = score;
                player1ScoreField.classList.add('animated-score');
                setTimeout(() => {
                    player1ScoreField.innerHTML = playerScore;
                    nextProblem();
                }, 300);
                setTimeout(() => {
                    if(player1ScoreField.classList.contains("animated-score")) {
                        player1ScoreField.classList.remove('animated-score');
                    }
                }, 1500);
            }
        });

        problemNumberBox.innerHTML = "Problem " + (currentProblemIndex + 1);
        problemImage.src = currentProblem.image;
        answerForm.addEventListener("submit", sendAnswer);
    });
}

var sendAnswer = function(event) {
    event.preventDefault();
    let playerAnswer = playerInput.value;
    playerInput.value = "";
    let message = {
        gameId: gameId,
        currentProblemId: currentProblemId,
        answer: playerAnswer
    }
    stompClient.send("/app/computer", {}, JSON.stringify(message));
}

function nextProblem() {
    //send game page back to front
    //gameContent.style.zIndex = 1;

    if (currentProblemIndex + 1 < numProblems) {
        currentProblemIndex++;
        currentProblem = problems[currentProblemIndex];
        currentProblemId = problems[currentProblemIndex].problemId;

        problemNumberBox.innerHTML = "Problem " + (currentProblemIndex + 1);
        problemImage.src = currentProblem.image;
    } else if (currentProblemIndex + 1 == numProblems) {
        endGame();
    }
}

function endGame() {
    stopTimer();
    alert("Game Ended!");
}


function convertToLatex() {
    //const userInput = document.getElementById('user-input')
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

async function animateBotScore() {
    for(let i = 0; i < botInfo.length; i++) {
        await delay(botInfo[i] * 1000);
        doScoreAnim();
    }
}

function delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function doScoreAnim() {
    botScore++;

    player2ScoreField.classList.add("animated-score");
    setTimeout(() => {
        player2ScoreField.innerHTML = botScore;
    }, 300);
    setTimeout(() => {
        if(player2ScoreField.classList.contains("animated-score")) {
            player2ScoreField.classList.remove('animated-score');
        }

        if(botScore === numProblems) {
            endGame();
        }
    }, 1500);
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

async function getUsername() {
    const response = await fetch('/username');
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const username = await response.text();
    return username;
}


setTimeout(() => {
    startGame();
}, 2000);