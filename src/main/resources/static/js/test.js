/* const modalEnd = document.getElementById('modal-end');
const endClose = document.getElementById("end-close"); */

/* window.addEventListener("beforeunload", (event) => {
    event.returnValue = "test";
}); */

var username;
var gameId;
var playerType;
var mins = 10;
setUpGame();

async function setUpGame() {
    username = await getUsername();
    gameId = "29e632f5-b37f-40c1-922c-c38f57f0d3a1";

    window.addEventListener("beforeunload", () => {
        sendDisconnect()
    });

    window.addEventListener("popstate", () => {
        sendDisconnect()
    });

    //playerType = await getPlayerType(gameId);
    //mins = await getTimeLimit(gameId);
}

async function sendDisconnect() {
    try {
        const reqBody = JSON.stringify({
            "gameId": gameId, "username": username
        });
        fetch("/game/disconnect", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: reqBody,
            keepalive: true
        });
    } catch(e) {
        console.log(e);
    }
}

async function getUsername() {
    const response = await fetch('/username');
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const username = await response.text();
    return username;
}

/* endClose.addEventListener("click", () => {
    let modalE = new bootstrap.Modal(modalEnd);
    modalE.hide();
});

showModal();

function showModal() {
    let modal = new bootstrap.Modal(modalEnd);
    modal.show();
} */
