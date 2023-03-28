const url = "http://localhost:8080";
let stompClient;
let gameId;

function connectToSocket(gameId) {
    console.log("connecting to the game");
    let socket = new SockJS(url + "/gameplay");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to the frame: " + frame);
        stompClient.subscribe("/topic/game-progress/" + gameId, function (response) {
            let data = JSON.parse(response.body);
            console.log(data);
            displayResponse(data);
        })
    })
}

/*async function getUsername(url = "http://localhost:8080/username") {
    const response = await fetch(url, {
      method: "GET", // *GET, POST, PUT, DELETE, etc.
      mode: "cors", // no-cors, *cors, same-origin
      cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
      credentials: "same-origin", // include, *same-origin, omit
      redirect: "follow", // manual, *follow, error
      referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
    });
    return response;
  }*/

async function getUsername() {
    const response = await fetch(url + '/username');
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const username = await response.text();
    return username;
}
  

function create_game() {
    fetch("/game/create")
        .then((response) => response.text()
            .then((text) => {
                const params = {
                    gameId: text
                };

                const queryParams = new URLSearchParams(params).toString();
                window.location.href = "/game";
            }));

    

    /*getUsername().then(username => {
        $.ajax({
            url: url + "/game/start",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "username": username
            }),
            success: function (data) {
                gameId = data.gameId;
                connectToSocket(gameId);
                alert("Your created a game. Game id is: " + data.gameId);
                window.location.replace(url + "/game?gameId=" + gameId);
            },
            error: function (error) {
                console.log(error);
            }
        })
    });*/   
}

function connectToRandom() {
    getUsername().then(username => {
        $.ajax({
            url: url + "/game/connect/random",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "username": username
            }),
            success: function (data) {
                gameId = data.gameId;
                connectToSocket(gameId);
                alert("Congrats, you're playing with: " + data.player1.username);
                window.location.replace(url + "/game?gameId=" + gameId);
            },
            error: function (error) {
                console.log(error);
            }
        })
    })   
}

function connectToSpecificGame() {
    let gameId = document.getElementById("game_id").value;
    if (gameId == null || gameId === '') {
        alert("Please enter game id");
    }
    getUsername().then(username => {
        $.ajax({
            url: url + "/game/connect",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "player": {
                    "username": username
                },
                "gameId": gameId
            }),
            success: function (data) {
                gameId = data.gameId;
                connectToSocket(gameId);
                alert("Congrats, you're playing with: " + data.player1.username);
                window.location.replace(url + "/game?gameId=" + gameId);
            },
            error: function (error) {
                console.log(error);
            }
        })
    })
}
