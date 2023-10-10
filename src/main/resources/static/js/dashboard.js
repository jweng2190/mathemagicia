/* let topBarUsername = document.getElementById("topbar_username");
let topBarLevel = document.getElementById("user_level");
let userXp = document.getElementById("user_xp");
let xpPoints = document.getElementById("xp_points");
const xpBarWidth = 200;

let gameIdInput = document.getElementById("game_id_input");

getUserInfo("Username").then((username) => {
    topBarUsername.textContent = username;
});

getUserInfo("Level").then((level) => {
    topBarLevel.textContent = level;
});

getUserXp().then((xp) => {
    let currentXp = xp[0];
    let xpLevelUp = xp[1];
    xpPoints.textContent = currentXp;
    let percentage = currentXp / xpLevelUp;
    let width = percentage * xpBarWidth;
    userXp.style.width = width + "px";
});


async function getUserInfo(type) {
    let response;
    if(type === "Username") {
        response = await fetch("/username");
    } else if(type === "Level") {
        response = await fetch("/level");
    }
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const responseText = await response.text();
    return responseText;
}

async function getUserXp() {
    const response = await fetch("/xp");
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const responseJson = await response.json();
    return responseJson;
}

async function getUsername() {
    const response = await fetch(url + '/username');
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const username = await response.text();
    return username;
} */

import { setUpBar } from "./home.js";

const cgButton = document.getElementById("cg_button");
const jgButton = document.getElementById("jg_button");

cgButton.addEventListener("click", createGame);
jgButton.addEventListener("click", joinGame);

setUpBar();


function createGame() {
    window.location.href = "/create_game";
}

function joinGame() {
    var gameId = gameIdInput.value;
    window.location.href = "/game/" + gameId;
}
