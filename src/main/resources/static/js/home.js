const badgeUrl = "https://mm-level-badges.s3.us-east-2.amazonaws.com";

let topBarUsername = document.getElementById("topbar_username");
let topBarBadge = document.getElementById("user_badge");
let topBarLevel = document.getElementById("user_level");
let userXp = document.getElementById("user_xp");
let xpPoints = document.getElementById("xp_points");
const xpBarWidth = 200;

export function setUpBar() {
    getName();
    getLevel();
    getXp();
}

setUpBar();

function getName() {
    if(sessionStorage.getItem("username") == null) {
        getUserInfo("Username").then((username) => {
            topBarUsername.textContent = username;
            sessionStorage.setItem("username", username);
        });
    } else {
        topBarUsername.textContent = sessionStorage.getItem("username");
    }
}

function getLevel() {
    if(sessionStorage.getItem("level") == null) {
        getUserInfo("Level").then((level) => {
            topBarLevel.textContent = level;
            getBadgeByLevel(level);
            sessionStorage.setItem("level", level);
        });
    } else {
        let level = sessionStorage.getItem("level");
        getBadgeByLevel(level);
        topBarLevel.textContent = level;
    }
}

function getXp() {
    if(sessionStorage.getItem("xp") == null) {
        getUserXp().then((xp) => {
            computeXp(xp);
        });
    } else {
        let xp = sessionStorage.getItem("xp");
        computeXp(xp);
    }
}

function getBadgeByLevel(level) {
    topBarBadge.src = badgeUrl + "/b" + level + ".png";
}

function computeXp(xp) {
    let currentXp = xp[0];
    let xpLevelUp = xp[1];
    xpPoints.textContent = currentXp;
    let percentage = currentXp / xpLevelUp;
    let width = percentage * xpBarWidth;
    userXp.style.width = width + "px";
}

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