import { getBaseUri } from "./get_base_uri.js";
import { getUsername } from "./user_data.js";
import { getSideBar } from "./sidebar.js";
import { getTopBar } from "./topbar.js";

const username = await getUsername();

let requestStatus = -1;
// -1 means not active
// 0 means cancel/cancelling
// 1 means joining/joined

const buttonJoin = document.getElementById("join_game");
//const testSpan = document.getElementById("test_span");
const divStatus = document.getElementById("status_div");
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
            let msgObject = JSON.parse(message.body);
            let type = msgObject.type;
            if(type === "status") {
                let status = msgObject.content;
                handleStatus(status);
            } else if(type === "creation") {
                let gameId = msgObject.content;
                console.log("Game ID: " + gameId);
                var baseUrl = window.location.protocol + "//" + window.location.host;
                window.location.href = baseUrl + "/game/" + gameId;
            }
        });

        buttonJoin.addEventListener("click", function() {
            if(requestStatus == -1 || requestStatus == 0) {
                requestStatus = 1;
                //testSpan.style.color = "green";
                divStatus.style.display = "";
                buttonJoin.textContent = "Cancel";
                buttonJoin.style.background = "#6c757d";
            } else {
                requestStatus = 0;
                //testSpan.style.color = "gray";
                divStatus.style.display = "none";
                buttonJoin.removeAttribute("style");
                buttonJoin.textContent = "Join";
            }
            requestGame(stompClient, requestStatus);
        });
    });
}

function requestGame(client, type) {
    let timestamp = Date.now();
    let msg = {
        type: (type === 1) ? "join": "cancel",
        username: username,
        timestamp: timestamp
    }
    client.send(`/ws/quick_play`, {}, JSON.stringify(msg));
}

function handleStatus(status) {
    console.log("Status: " + status);
}