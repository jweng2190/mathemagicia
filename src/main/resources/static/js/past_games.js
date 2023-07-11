var gameList;
getGames().then((data) => {
    gameList = data;
    console.log(gameList);
});

async function getGames() {
    const response = await fetch("/all_games");
    const listGames = await response.json();
    return listGames;
}