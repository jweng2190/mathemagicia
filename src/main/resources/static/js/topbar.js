const badgeUrl = "https://mm-level-badges.s3.us-east-2.amazonaws.com";
const xpBarWidth = 200;

let topBarUsername, topBarBadge, topBarLevel, userXp, xpPoints;

export async function getTopBar() {
    try {
        const response = await fetch("/templates/template_topbar.html");
        if(!response.ok) {
            throw new Error("Unable to get html template code!");
        }
        const result = await response.text();

        const container = document.createElement("div");
        container.innerHTML = result;
        //const firstElem = container.firstChild;
        const templateContent = container.querySelector('template').content.cloneNode(true);

        document.body.insertBefore(templateContent, document.body.firstChild);
        //console.log(result);
    } catch(error) {
        console.error('Error during fetch: ' + error);
    }

    topBarUsername = document.getElementById("topbar_username");
    topBarBadge = document.getElementById("user_badge");
    topBarLevel = document.getElementById("user_level");
    userXp = document.getElementById("user_xp");
    xpPoints = document.getElementById("xp_points");

    getName();
    const userLevel = await getLevel();
    getXp();
    return userLevel;
}

async function getName() {
    const username = await getUserInfo("Username");
    topBarUsername.textContent = username;
}

async function getLevel() {
    const level = await getUserInfo("Level");
    topBarLevel.textContent = level;
    getBadgeByLevel(level);
    return level;
}

async function getXp() {
    const xp = await getUserXp();
    computeXp(xp);
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
        response = await fetch("/user_level");
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
