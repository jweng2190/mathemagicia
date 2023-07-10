// Wait for the DOM to load
document.addEventListener('DOMContentLoaded', function () {
  // Select the sliding windows
  const leftWindow = document.getElementById('left-window');
  const rightWindow = document.getElementById('right-window');

  // Animate the sliding windows
  setTimeout(() => {
    leftWindow.style.animation = 'slide-in-left 1.5s forwards';
    rightWindow.style.animation = 'slide-in-right 1.5s forwards';

    const player1ScoreElement = document.getElementById('player1-score');
    const player2ScoreElement = document.getElementById('player2-score');

    leftWindow.addEventListener('animationend', function () {
      player1ScoreElement.style.animation = 'score-animation 1.5s';
      setTimeout(() => {
        player1ScoreElement.innerText = 1;
      }, 300);
      player1ScoreElement.addEventListener('animationend', function () {
        leftWindow.style.animation = 'slide-out-left 1.5s forwards';
      });
    });

    rightWindow.addEventListener('animationend', function () {
      player2ScoreElement.style.animation = 'score-animation 1.5s';
      setTimeout(() => {
        player2ScoreElement.innerText = 2;
      }, 300);
    });
    player2ScoreElement.addEventListener('animationend', function () {
      rightWindow.style.animation = 'slide-out-right 1.5s forwards';
    });
  }, 500); // Delay the animation to allow fading
});