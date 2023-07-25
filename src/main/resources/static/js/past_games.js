var gameList = [];
getGames().then((data) => {
    gameList = data;
    console.log(gameList);
    populateTable(gameList);
});

async function getGames() {
    const response = await fetch("/all_games");
    const listGames = await response.json();
    return listGames;
}

function populateTable(gameList) {
    for(var i = 0; i < gameList.length; i++) {
        var game = gameList[i];
        console.log(game.player1Username);
    }
}
