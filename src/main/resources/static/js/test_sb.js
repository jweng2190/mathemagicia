let buttonShow = document.getElementById("button_show");
let modal = new bootstrap.Modal(document.getElementById("modal-1"), {});
const scoreCounter = document.getElementById('earned_xp');
const targetScore = 100; // Set the target score
const increment = 1;
let animationStep = 0;

function animateStep() {
    const divElement = document.getElementById('current_xp');
    switch (animationStep) {
        case 0:
            divElement.style.width = '200px';
            break;
        case 1:
            divElement.style.width = '0';
            break;
        case 2:
            divElement.style.width = '100px';
            break;
    }
    animationStep = (animationStep + 1) % 3; // Loop through animation steps
}

function repeatAnimation() {
    let numSteps = 0;
    const interval = setInterval(() => {
        animateStep();
        numSteps++;
        if(numSteps >= 3) {
            clearInterval(interval);
        }
    }, 1250);
}

function animateScore() {
    let currentScore = 0;

    const interval = setInterval(() => {
        currentScore += increment;
        scoreCounter.textContent = "+" + currentScore;

        if (currentScore >= targetScore) {
            clearInterval(interval);
            animateWidth();
        }
    }, 1000 / (targetScore / increment));
}

function animateWidth() {
    const divElement = document.getElementById('current_xp');
    const targetWidth = 190; // Set the desired final width
    divElement.style.width = targetWidth + 'px'; // Update the width dynamically
    setTimeout(() => {
        startConfetti();
        setTimeout(() => {
            stopConfetti();
        }, 5000);
    }, 1000);
}

buttonShow.addEventListener("click", () => {
    modal.show();
    animateScore();
});

function closeModal() {
    modal.hide();
}

