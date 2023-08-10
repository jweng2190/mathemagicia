const countdownElement = document.getElementById('countdown_text');
const countdownContainer = document.getElementsByClassName('countdown-container')[0];
const content = document.getElementsByClassName('content')[0];
const texts = ['READY', '3', '2', '1', 'GO!'];
const colors = ['rgb(0, 150, 255)', 'rgb(222, 49, 99)', 'rgb(255, 117, 24)', 'rgb(255, 191, 0)', 'rgb(15, 255, 80)']
let index = 0;

const backgroundDiv = document.getElementById('cd-bg-div');

function testFadeOutBg() {
    backgroundDiv.style.opacity = '0';
    document.getElementsByClassName('content')[0].style.opacity = '1';
    backgroundDiv.style.zIndex = '-2';
    countdownContainer.style.zIndex = '-2';
}


function testCDTimer() {
    setTimeout(testFadeInAndOut, 1000);
}

function testFadeInAndOut() {
    countdownElement.style.opacity = '1';
    countdownElement.innerHTML = texts[index];
    countdownElement.style.color = colors[index];
    setTimeout(() => {
        countdownElement.style.opacity = '0';
        index++;
        if (index < texts.length) {
            setTimeout(testFadeInAndOut, 500);
        }
    }, 500);
}

function testCD() {
    setTimeout(testFadeOutBg, 6000);
    testCDTimer();
}