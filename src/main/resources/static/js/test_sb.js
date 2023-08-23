let buttonShow = document.getElementById("button_show");
let modal = new bootstrap.Modal(document.getElementById("modal-1"), {});
const scoreCounter = document.getElementById('earned_xp');
const targetScore = 100; // Set the target score
let animationStep = 0;
const listStats = [[1, 480, 500], [2, 0, 1000], [20]];


function animateStep(stats) {
    const playerLevel = document.getElementById('player_level');
    const newLevel = stats[1][0];
    const newXp = stats[1][1];
    const totalXp = stats[1][2];
    const newWidth = (newXp / totalXp) * 200;
    const divElement = document.getElementById('current_xp');

    switch (animationStep) {
        case 0:
            divElement.style.width = '200px';
            break;
        case 1:
            playerLevel.style.animation = "score-animation 1.5s";
            setTimeout(() => {
                playerLevel.textContent = newLevel;
            }, 300); 
            divElement.style.width = '0';
            break;
        case 2:
            divElement.style.width = newWidth + "px";
            break;
    }
    animationStep = (animationStep + 1) % 3; // Loop through animation steps
}

function repeatAnimation(stats) {
    let numSteps = 0;
    const interval = setInterval(() => {
        animateStep(stats);
        numSteps++;
        if(numSteps >= 3) {
            clearInterval(interval);
            congrats();
        }
    }, 1250);
}

async function animateXp(gameId, playerType, stats) {
    const targetScore = stats[2][0];
    const increment = 1;
    let currentScore = 0;

    const originalLevel = stats[0][0];
    const level = stats[1][0];

    const interval = setInterval(() => {
        currentScore += increment;
        scoreCounter.textContent = "+" + currentScore;

        if (currentScore >= targetScore) {
            clearInterval(interval);
            if(originalLevel < level) {
                repeatAnimation(stats);
            } else {
                animateWidth(stats);
            }
        }
    }, 1000 / (targetScore / increment));
}

function animateWidth(stats) {
    const divElement = document.getElementById('current_xp');
    const finalXp = stats[1][1];
    const totalXp = stats[1][2];
    const targetWidth = (finalXp / totalXp) * 200; // Set the desired final width
    divElement.style.width = targetWidth + 'px'; // Update the width dynamically
    congrats();
}

function congrats() {
    setTimeout(() => {
        startConfetti();
        setTimeout(() => {
            stopConfetti();
        }, 5000);
    }, 1000);
}

buttonShow.addEventListener("click", () => {
    modal.show();
    animateXp("b0805641-d258-4233-aecc-e264357c7b1a", "Player 1", listStats);
});

function closeModal() {
    modal.hide();
}

async function getXpEarned(gameId, type) {
    const requestBody = {
        gameId: gameId,
        playerType: type
    }
    const requestJsonValue = JSON.stringify(requestBody);
    const response = await fetch("/game/xp_earned", {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        mode: "cors", // no-cors, *cors, same-origin
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        credentials: "same-origin", // include, *same-origin, omit
        headers: {
            "Content-Type": "application/json",
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: requestJsonValue, // body data type must match "Content-Type" header
    });

    const responseText = await response.text();
    return responseText;
}

async function getXpInfo(gameId, type) {
    const requestBody = {
        gameId: gameId,
        playerType: type
    }
    const requestJsonValue = JSON.stringify(requestBody);
    const response = await fetch("/xp/level_up", {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        mode: "cors", // no-cors, *cors, same-origin
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        credentials: "same-origin", // include, *same-origin, omit
        headers: {
            "Content-Type": "application/json",
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: requestJsonValue, // body data type must match "Content-Type" header
    });

    const responseBody = await response.json();
    return responseBody;
}

