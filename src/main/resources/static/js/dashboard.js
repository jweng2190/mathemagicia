/* let createGameLink = document.getElementById('createGameLink');
let joinGameLink = document.getElementById('joinGameLink');
let thirdItemLink = document.getElementById('thirdItemLink');

createGameLink.addEventListener('click', handleCreateGameClick);
joinGameLink.addEventListener('click', handleJoinGameClick);
thirdItemLink.addEventListener('click', handleThirdItemClick);

let currentActiveLink = createGameLink;

function handleCreateGameClick() {
    // Call your specific function for 'Create Game'
    // Example:
    console.log("Create Game clicked");
    currentActiveLink.className = "nav-link";
    createGameLink.className = "nav-link active";
    currentActiveLink = createGameLink;
    cardTitle.innerHTML = "Create Game";
    cardText.innerHTML = "Click the button below to create a 1v1 game.";
    dashboardButton.innerHTML = "Create";
    //gameIdDiv.style.display = "none";
} */


function createGame() {
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
}