function create_game() {
    fetch("/game/create")
        .then((response) => response.text()
            .then((text) => {
                gameId = text;

                alert(gameId);
                window.location.href = "/game/" + gameId;
            }));
}