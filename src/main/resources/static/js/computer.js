let easyDif = document.getElementById("easy");
let mediumDif = document.getElementById("medium");
let hardDif = document.getElementById("hard");
let lightning = document.getElementById("lightning");
let speed = document.getElementById("speed");
let standard = document.getElementById("standard");
const diffButton = document.getElementById("diff_button");
const timeButton = document.getElementById("time_button");
const formCreate = document.getElementById("cg_form");
const inputDiff = document.getElementById("difficulty");
const inputTime = document.getElementById("time");
const createBotUrl = '/game/computer';
const diffSpan = document.getElementById("diff_disp");
const diffInput = document.getElementById("diff");
const pStatus = document.getElementById("status");

easyDif.addEventListener('click', function() {
    handleDifficulty("easy");
});
mediumDif.addEventListener('click', function() {
    handleDifficulty("medium");
});
hardDif.addEventListener('click', function() {
    handleDifficulty("hard");
});
lightning.addEventListener("click", function() {
    handleTimeControl("lightning");
});
speed.addEventListener("click", function() {
    handleTimeControl("speed");
});
standard.addEventListener("click", function() {
    handleTimeControl("standard");
});
diffInput.addEventListener("change", change);

formCreate.addEventListener('submit', function (event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const difficulty = formData.get('difficulty');
    const time = formData.get('time');
    const computerLevel = formData.get('computerLevel');
    const params = new URLSearchParams();
    params.append('difficulty', difficulty);
    params.append('time', time);
    params.append('computerLevel', computerLevel);

    console.log('difficulty:', difficulty);
    console.log('time:', time);
    console.log('computerLevel:', computerLevel);

    fetch(createBotUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params
    })
        .then(response => response.json())
        .then(gameList => {
            console.log(gameList);
            const gameId = gameList[0];
            const botLevel = gameList[1];
            sessionStorage.setItem("gameId", gameId);
            sessionStorage.setItem("computerLevel", botLevel);
            pStatus.style.color = 'green';
            pStatus.innerHTML = 'Game successfully created!';
            setTimeout(() => {
                window.location.href = '/computer/' + botLevel;
            }, 2000);
        })
        .catch(error => {
            const errorMsg = "Error: Something went wrong. Try again later."
            pStatus.style.color = 'red';
            pStatus.innerHTML = errorMsg;
            //console.error(errorMsg);
        });
});

function handleDifficulty(difficulty) {
    const formatDiff = difficulty.charAt(0).toUpperCase() + difficulty.slice(1);
    diffButton.innerHTML = formatDiff;
    inputDiff.value = difficulty;
    if(difficulty === "easy") {
        diffButton.style.background = "#50C878";
    } else if(difficulty === "medium") {
        diffButton.style.background = "#FDDA0D";
    } else if(difficulty === "hard") {
        diffButton.style.background = "#D22B2B";
    }
}

function handleTimeControl(time) {
    let timeImg = document.createElement('img');
    timeImg.style.height = "25px";
    timeImg.style.width = "auto";
    timeImg.style.marginLeft = "17px";
    if(time === "lightning") {
        timeButton.textContent = "5:00";
        inputTime.value = "5:00";
        timeImg.src = "/img/flash.png";
    } else if(time === "speed") {
        timeButton.textContent = "10:00";
        inputTime.value = "10:00";
        timeImg.src = "/img/hourglass.png";
    } else if(time === "standard") {
        timeButton.textContent = "20:00";
        inputTime.value = "20:00";
        timeImg.src = "/img/timer.png";
    }
    timeButton.appendChild(timeImg);
}

function change() {
    diffSpan.textContent = diffInput.value;
}