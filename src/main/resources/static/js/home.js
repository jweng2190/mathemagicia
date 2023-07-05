let cardText = document.getElementsByClassName("card-text")[0];
let cardTitle = document.getElementsByClassName("card-title")[0];

let button = document.getElementById("dashboard_button");

let createGameLink = document.getElementById('createGameLink');
let joinGameLink = document.getElementById('joinGameLink');
let thirdItemLink = document.getElementById('thirdItemLink');

let gameIdDiv = document.getElementsByClassName("group")[0];
let gameIdInput = document.getElementsByClassName("game_id_input")[0];

let currentActiveLink = createGameLink;

// Add click event listeners to the list elements
createGameLink.addEventListener('click', handleCreateGameClick);
joinGameLink.addEventListener('click', handleJoinGameClick);
thirdItemLink.addEventListener('click', handleThirdItemClick);

button.addEventListener("click", handleClickButton);

// Function to handle click on 'Create Game'
function handleCreateGameClick() {
    // Call your specific function for 'Create Game'
    // Example:
    console.log("Create Game clicked");
    currentActiveLink.className = "nav-link";
    createGameLink.className = "nav-link active";
    currentActiveLink = createGameLink;
    cardTitle.innerHTML = "Create Game";
    cardText.innerHTML = "Click the button below to create a 1v1 game.";
    button.innerHTML = "Create";
    gameIdDiv.style.display = "none";
}

// Function to handle click on 'Join Game'
function handleJoinGameClick() {
    // Call your specific function for 'Join Game'
    // Example:
    console.log("Join Game clicked");
    currentActiveLink.className = "nav-link";
    joinGameLink.className = "nav-link active";
    currentActiveLink = joinGameLink;
    cardTitle.innerHTML = "Join Game";
    cardText.innerHTML = "Click the button below to join a 1v1 game.";
    button.innerHTML = "Join";
    gameIdDiv.style.display = "block";
}

// Function to handle click on 'Third Item'
function handleThirdItemClick() {
    // Call your specific function for 'Third Item'
    // Example:
    console.log("Third Item clicked");
    currentActiveLink.className = "nav-link";
    thirdItemLink.className = "nav-link active";
    currentActiveLink = thirdItemLink;
    cardTitle.innerHTML = "Bruh";
    cardText.innerHTML = "Haven't decided yet";
    button.innerHTML = "IDK";
    gameIdDiv.style.display = "none";
}

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

function joinGame() {
    gameId = gameIdInput.value;
    gameIdInput.value = "";
    alert("You are joining the game with a game ID: " + gameId);
    window.location.href = "/game/" + gameId;
}

function handleClickButton() {
    if(currentActiveLink == createGameLink) {
        createGame();
    } else if(currentActiveLink == joinGameLink) {
        joinGame();
    } else if(currentActiveLink == thirdItemLink) {
        //do nothing
    }
}