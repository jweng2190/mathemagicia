import { getBaseUri } from "./get_base_uri.js";
import { getUsername } from "./user_data.js";
import { getSideBar } from "./sidebar.js";
import { getTopBar } from "./topbar.js";

const username = await getUsername();

const buttonJoin = document.getElementById("join_game");
const base_uri = getBaseUri();
connectQueue();
getSideBar("regular");
getTopBar();

function connectQueue() {
    const wsQuickPlay = new WebSocket(base_uri + "/qp");
    let stompClient = Stomp.over(wsQuickPlay);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe('/user/' + username + "/quick_play", function (message) {
            let gameId = message.body;
            console.log("Game ID: " + gameId);
        });

        buttonJoin.addEventListener("click", function() {
            queueGame(stompClient);
        });
    });
}

function queueGame(client) {
    client.send(`/ws/quick_play`, {}, username);
}