import { getTopBar } from "./topbar.js";
import { getSideBar } from "./sidebar.js";

const cgButton = document.getElementById("cg_button");
const jgButton = document.getElementById("jg_button");

cgButton.addEventListener("click", createGame);
jgButton.addEventListener("click", joinGame);

getTopBar();
getSideBar("regular");


function createGame() {
    window.location.href = "/create_game";
}

function joinGame() {
    var gameId = gameIdInput.value;
    window.location.href = "/game/" + gameId;
}
