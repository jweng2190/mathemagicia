import { getSideBar } from "./sidebar.js";

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
const createUrl = '/game/create';

getSideBar("regular");

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

formCreate.addEventListener('submit', function (event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const difficulty = formData.get('difficulty');
    const time = formData.get('time');
    const params = new URLSearchParams();
    params.append('difficulty', difficulty);
    params.append('time', time);

    console.log('difficulty:', difficulty);
    console.log('time:', time);

    //send create game POST request
    fetch(createUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params
    })
        .then(response => response.text())
        .then(gameId => {
            console.log(gameId);
            const successMsg = "You have successfully created a " + time + " game with " + difficulty + " difficulty!";
            Toastify({
                text: successMsg,
                duration: 3000, // Display for 3 seconds
                close: true,
                stopOnFocus: true,
                style: {
                    background: "linear-gradient(to right, #00b09b, #96c93d)",
                }
            }).showToast();
            setTimeout(() => {
                window.location.href = '/invite_friend';
            });
        })
        .catch(error => {
            console.error('Error:', error);
            const errorMsg = "Error: Something went wrong. Try again later."
            Toastify({
                text: errorMsg,
                duration: 3000, // Display for 3 seconds
                close: true,
                stopOnFocus: true,
                style: {
                    background: "linear-gradient(to right, #00b09b, #96c93d)",
                }
            }).showToast();
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

/* function create_game() {
    fetch("/game/create")
        .then((response) => {
            if (!response.ok) {
                alert("Error: cannot create more than 1 game at the same time!");
            } else {
            response.text()
                .then((text) => {
                    gameId = text;

                    alert(gameId);
                    sessionStorage.setItem("gameId", gameId);

                    window.location.href = "/invite_friend";
                })
        }});
} */