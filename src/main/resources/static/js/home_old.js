let createGameLink = document.getElementById('createGameLink');
let joinGameLink = document.getElementById('joinGameLink');
let thirdItemLink = document.getElementById('thirdItemLink');

createGameLink.addEventListener('click', handleCreateGameClick);
joinGameLink.addEventListener('click', handleJoinGameClick);
thirdItemLink.addEventListener('click', handleThirdItemClick);

let currentActiveLink = createGameLink;

let cardText = document.getElementsByClassName("card-text")[0];
let cardTitle = document.getElementsByClassName("card-title")[0];

let dashboardButton = document.getElementById("dashboard_button");
// Add click event listeners to the list elements
dashboardButton.addEventListener("click", handleClickButton);

let pastGamesButton = document.getElementById('past_games_button');
pastGamesButton.addEventListener("click", pastGames);

let homeLink = document.getElementById("home_link");
let gameDashLink = document.getElementById("game_dashboard_link");
let sidebarLink = homeLink;

homeLink.addEventListener("click", handleHomeLink);
gameDashLink.addEventListener("click", handleGameDashLink);


let gameIdDiv = document.getElementsByClassName("group")[0];
let gameIdInput = document.getElementsByClassName("game_id_input")[0];

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
    dashboardButton.innerHTML = "Create";
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
    dashboardButton.innerHTML = "Join";
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
    dashboardButton.innerHTML = "IDK";
    gameIdDiv.style.display = "none";
}

function handleHomeLink() {
    sidebarLink.classList.remove("active", "link-light");
    homeLink.classList.add("active", "link-light");
    sidebarLink = homeLink;
}

function handleGameDashLink() {
    sidebarLink.classList.remove("active", "link-light");
    gameDashLink.classList.add("active", "link-light");
    sidebarLink = gameDashLink;
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

function pastGames() {
    window.location.href = "/past_games";
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