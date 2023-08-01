const url = "http://localhost:8080";
var topBarUsername = document.getElementById("topbar_username");
getUsername().then((username) => {
    topBarUsername.textContent = username; 
});

var gameIdInput = document.getElementById("game_id_input");


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
    var gameId = gameIdInput.value;
    window.location.href = "/game/" + gameId;
}

async function getUsername() {
    const response = await fetch(url + '/username');
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const username = await response.text();
    return username;
}