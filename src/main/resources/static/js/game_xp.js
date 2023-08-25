export async function getXpInfo(gameId, type) {
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

export async function animateXp(gameId, playerType) {
    let stats;
    try {
        stats = await getXpInfo(gameId, playerType);
    } catch(e) {
        throw new Error(e);
    }
    const playerLevel = document.getElementById("player_level");
    const xpValue = document.getElementById("xp_value");
    const xpBar = document.getElementById("current_xp");
    let currentXp = stats[0][1];
    let xpLevelUp = stats[0][2];
    playerLevel.textContent = stats[0][0];
    xpValue.textContent = currentXp;
    const width = (currentXp / xpLevelUp) * 200;
    xpBar.style.width = width + "px";

    const targetScore = stats[2][0];
    const increment = 1;
    let currentScore = 0;

    const originalLevel = stats[0][0];
    const level = stats[1][0];
    const scoreCounter = document.getElementById('earned_xp');

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

export function repeatAnimation(stats) {
    let numSteps = 0;
    const interval = setInterval(() => {
        animateStep(stats, numSteps);
        numSteps++;
        if(numSteps >= 3) {
            clearInterval(interval);
            showConfetti();
        }
    }, 1250);
}

export function animateWidth(stats) {
    const divElement = document.getElementById('current_xp');
    const finalXp = stats[1][1];
    const totalXp = stats[1][2];
    const targetWidth = (finalXp / totalXp) * 200; // Set the desired final width
    divElement.style.width = targetWidth + 'px'; // Update the width dynamically
    const xpValue = document.getElementById("xp_value");
    xpValue.textContent = finalXp;
    showConfetti();
}

export function animateStep(stats, animationStep) {
    const playerLevel = document.getElementById('player_level');
    const newLevel = stats[1][0];
    const newXp = stats[1][1];
    const totalXp = stats[1][2];
    const newWidth = (newXp / totalXp) * 200;
    const divElement = document.getElementById('current_xp');

    const xpValue = document.getElementById("xp_value");

    switch (animationStep) {
        case 0:
            divElement.style.width = '200px';
            break;
        case 1:
            playerLevel.style.animation = "level-animation 1.5s";
            setTimeout(() => {
                playerLevel.textContent = newLevel;
            }, 300); 
            divElement.style.width = '0px';
            break;
        case 2:
            divElement.style.width = newWidth + "px";
            xpValue.textContent = newXp;
            break;
    }
}

export function congrats() {
    setTimeout(() => {
        startConfetti();
        setTimeout(() => {
            stopConfetti();
        }, 5000);
    }, 1000);
}

function showConfetti() {
    var myCanvas = document.createElement('canvas');
    myCanvas.style.zIndex = "2";
    myCanvas.style.position = "fixed";
    myCanvas.style.height = "100vh";
    myCanvas.style.width = "100vw";

    document.body.appendChild(myCanvas);

    var myConfetti = confetti.create(myCanvas, {
        resize: true,
        gravity: 0.5
    });
    setTimeout(() => {
        myConfetti();
    }, 1000);
}
