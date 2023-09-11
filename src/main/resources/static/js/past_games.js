var gameList = [];
var table = document.getElementById("past_games");
const templNoGames = document.getElementById("no_games");
const mainDiv = document.querySelector('div');
const pageDisp = document.getElementById("currentPage")

// Constants for pagination
const itemsPerPage = 10;
var totalPages;
let currentPage = 1;

document.getElementById("nextPage").addEventListener("click", () => {
    if (currentPage < totalPages) {
        currentPage++;
        populateTable(currentPage);
    }
});

document.getElementById("prevPage").addEventListener("click", () => {
    if (currentPage > 1) {
        currentPage--;
        populateTable(currentPage);
    }
});

getGames().then((data) => {
    gameList = data;
    totalPages = Math.ceil(gameList.length / itemsPerPage);
    pageDisp.textContent = "Page 1\/" + totalPages;
    console.log(gameList);
    populateTable(currentPage);
});

async function getGames() {
    const response = await fetch("/all_games");
    const listGames = await response.json();
    return listGames;
}

function populateTable(page) {
    if(gameList.length == 0) {
        const noGames = document.importNode(templNoGames.content, true);
        mainDiv.appendChild(noGames);
    }

    table.innerHTML = "";
    const startIndex = (page - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;

    for(let i = startIndex; i < endIndex && i < gameList.length; i++) {
        if(gameList[i] != null) {
            var game = gameList[i];
            var row = table.insertRow();
            row.id = (i+1).toString(10);
            var cellPlayers = row.insertCell();
            var cellResult = row.insertCell();
            var cellReview = row.insertCell();
            var cellDate = row.insertCell();

            cellReview.style.verticalAlign = "middle";
            cellDate.style.verticalAlign = "middle";

            let gameId = game.gameId;

            stylePlayersCell(game, cellPlayers);
            styleResultCell(game, cellResult);
            styleReviewCell(gameId, cellReview);
            styleDateCell(game, cellDate);
        }
    }
    pageDisp.textContent = `Page ${currentPage}/` + totalPages;
}

function stylePlayersCell(game, cell) {
    var div1 = document.createElement("div");
    var div2 = document.createElement("div");

    div1.className = "d-flex";
    div2.className = "d-flex";

    var img1 = document.createElement("img");
    img1.style.width = "25px";
    img1.style.height = "25px";
    img1.style.marginRight = "18px";

    var img2 = document.createElement("img");
    img2.style.width = "25px";
    img2.style.height = "25px";
    img2.style.marginRight = "18px";

    var p1 = document.createElement("p");
    p1.style.width = "75%";
    p1.style.height = "30px";
    var span1 = document.createElement("span");
    span1.style.color = "rgb(0, 0, 0)";
    span1.style.backgroundColor = "transparent";
    span1.textContent = game.player1Username;
    p1.appendChild(span1);

    var p2 = document.createElement("p");
    p2.style.width = "75%";
    p2.style.height = "30px";
    var span2 = document.createElement("span");
    span2.style.color = "rgb(0, 0, 0)";
    span2.style.backgroundColor = "transparent";
    span2.textContent = game.player2Username;
    p2.appendChild(span2);

    div1.appendChild(img1);
    div1.appendChild(p1);

    div2.appendChild(img2);
    div2.appendChild(p2);

    cell.appendChild(div1);
    cell.appendChild(div2);
}

function styleResultCell(game, cell) {
    var player1Score = game.player1Score;
    var player2Score = game.player2Score;

    var div1 = document.createElement("div");
    var div2 = document.createElement("div");

    div1.className = "d-flex";
    div2.className = "d-flex";

    var p1 = document.createElement("p");
    p1.style.width = "25%";
    p1.innerHTML = player1Score;

    var p2 = document.createElement("p");
    p2.style.width = "25%";
    p2.innerHTML = player2Score;

    var img1 = document.createElement("img");
    img1.style.width = "25px";
    img1.style.height = "25px";
    if(player1Score > player2Score) {
        img1.src = "/img/green_plus.svg";
    } else if(player2Score > player1Score) {
        img1.src = "/img/red_minus.svg";
    } else {
        img1.src = "/img/gray_equals.svg"
    }

    var img2 = document.createElement("img");
    img2.style.width = "25px";
    img2.style.height = "25px";
    if(player2Score > player1Score) {
        img2.src = "/img/green_plus.svg";
    } else if(player1Score > player2Score) {
        img2.src = "/img/red_minus.svg";
    } else {
        img2.src = "/img/gray_equals.svg"
    }

    div1.append(p1);
    div1.append(img1);
    div2.append(p2);
    div2.append(img2);

    cell.append(div1);
    cell.append(div2);
}

function styleReviewCell(gameId, cell) {
    var linkReview = document.createElement("a");
    linkReview.innerHTML = "Details";
    linkReview.href = "/review/" + gameId;
    //nothing right now

    cell.append(linkReview);
}

function styleDateCell(game, cell) {
    //TODO
    var date = game.gameDate;
    //console.log(date);
    var month = date[1];
    var day = date[2];
    var year = date[0];

    var dateString = month + "/" + day + "/" + year;

    cell.innerHTML = dateString;
}