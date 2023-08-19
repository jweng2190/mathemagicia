let topBarLevel = document.getElementById("user_level");
let userXp = document.getElementById("user_xp");
const xpBarWidth = 200;

getUserInfo("Level").then((level) => {
    topBarLevel.textContent = level;
});

getUserXp().then((xp) => {
    let currentXp = xp[0];
    let xpLevelUp = xp[1];
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