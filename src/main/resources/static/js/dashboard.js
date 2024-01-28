import { getTopBar } from "./topbar.js";
import { getSideBar } from "./sidebar.js";

var loc = window.location, base_uri;
if (loc.protocol === "https:") {
    base_uri = "wss:";
} else {
    base_uri = "ws:";
}
base_uri += "//" + loc.host;

const username = await getUsername();
let gameId;
var loc = window.location.protocol + "//" + window.location.host;
let shareM = document.getElementById("modal-share");
var modalShare = new bootstrap.Modal(shareM, {backdrop: 'static', keyboard: false}); 
var inputLink = document.getElementById("game_link");
var copyButton = document.getElementById("copy_link");

const cgButton = document.getElementById("cg_button");
/* const jgButton = document.getElementById("jg_button"); */

const statusCell = document.getElementById("status_game");

const shareButton = document.getElementById("share");
const joinButton = document.getElementById("join");
const deleteButton = document.getElementById("delete");

const contentRow = document.getElementById("game_data");
const cells = contentRow.getElementsByTagName('td');

cgButton.addEventListener("click", createGame);
//jgButton.addEventListener("click", joinGame);
copyButton.addEventListener("click", copyToClipboard);

connectStatus();
getTopBar();
getSideBar("regular");
getActiveGame();

function createGame() {
    window.location.href = "/create_game";
}

async function getActiveGame() {
    try {
        const response = await fetch('/game/active_game', {
            method: "POST", // *GET, POST, PUT, DELETE, etc.
            mode: "cors", // no-cors, *cors, same-origin
            cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
            credentials: "same-origin", // include, *same-origin, omit
            headers: {
                "Content-Type": "text/plain",
            },
            redirect: "follow", // manual, *follow, error
            referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
            body: username, // body data type must match "Content-Type" header
        });

        const game = await response.json();
        gameId = game[3];

        shareButton.addEventListener("click", shareGame);
        joinButton.addEventListener("click", joinGame);
        deleteButton.addEventListener("click", deleteGame);
        populateTable(game);
    } catch(error) {
        console.log("No created games found!");
    }
}

async function populateTable(gameData) {
    let cellDiff = cells[0];
    let cellTime = cells[1];
    let cellDate = cells[2];

    let gameDiff, gameTime, gameDate;
    gameDiff = gameData[0], gameTime = gameData[1], gameDate = gameData[2];

    cellDiff.textContent = gameDiff.charAt(0).toUpperCase() + gameDiff.slice(1);
    cellTime.textContent = gameTime;

    let month = gameDate[1];
    let day = gameDate[2];
    let year = gameDate[0];
    let dateString = month + "/" + day + "/" + year;
    cellDate.textContent = dateString;

    contentRow.style.display = "";
}

async function hideTable() {
    let cellDiff = cells[0];
    let cellTime = cells[1];
    let cellDate = cells[2];
    cellDiff.textContent = ""; cellTime.textContent = ""; cellDate.textContent = "";
    contentRow.style.display = "none";
}

async function getUsername() {
    try {
        const response = await fetch("/username");
        const result = await response.text();
        return result;
    } catch(error) {
        console.error(error);
    }
}

function shareGame() {
    //TODO, popup modal
    inputLink.value = loc + "/game/" + gameId; 
    modalShare.show();
}

function joinGame() {
    window.location.href = "/game/" + gameId;
}

async function deleteGame() {
    hideTable();
    const reqBody = JSON.stringify({
        "username": username, "gameId": gameId
    });
    try {
        const response = await fetch('/game/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: reqBody
        });

        const result = await response.text();
        console.log(result);
    } catch(error) {
        console.log(error);
    }
}

function copyToClipboard() {
    inputLink.select();
    inputLink.setSelectionRange(0, 99999);
    document.execCommand("copy");
    showNotification();
}

function showNotification() {
    // Get the notification element
    var notificationElement = document.getElementById("notification");

    // Show the notification
    notificationElement.style.display = "block";

    // Hide the notification after a certain duration (e.g., 3 seconds)
    setTimeout(function() {
        notificationElement.style.display = "none";
    }, 3000);
}

function connectStatus() {
    const socket = new WebSocket(base_uri + "/created_status", "v10.stomp");
    const stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe('/user/' + username + '/created_status', function (message) {
            if(message.body === "joined") {
                console.log("Player 2 Joined!");
                statusCell.textContent = "Ready";
                statusCell.style.color = "green";
            } else if(message.body === "disconnect") {
                statusCell.textContent = "Not Ready";
                statusCell.style.color = "red";
                console.log("Player 2 left.")
            }
        });
    });
}