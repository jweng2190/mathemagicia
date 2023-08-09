const countdownElement = document.getElementById('countdown_text');
const texts = ['READY', '3', '2', '1', 'GO!'];
const colors = ['rgb(0, 150, 255)', 'rgb(222, 49, 99)', 'rgb(255, 117, 24)', 'rgb(255, 191, 0)', 'rgb(80, 200, 120)']
let index = 0;

function startTimer() {
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

startTimer();