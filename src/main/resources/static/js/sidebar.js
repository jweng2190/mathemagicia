export async function getSideBar(type) {
    try {
        const response = await fetch("/templates/template_sidebar.html");
        if(!response.ok) {
            throw new Error("Unable to get html template code!");
        }
        const result = await response.text();

        const container = document.createElement("div");
        container.innerHTML = result;
        //const firstElem = container.firstChild;
        const templateContent = container.querySelector('template').content.cloneNode(true);
        switch(type) {
            case "regular":
                document.body.insertBefore(templateContent, document.body.firstChild);
                break;
            case "game":
                const gameContainer = document.querySelector('.game-page');

                if(gameContainer) {
                    gameContainer.insertBefore(templateContent, gameContainer.firstChild);
                } else {
                    console.error("Oops! Could not get game content layout!");
                }
                break;
        }
    } catch(error) {
        console.error('Error during fetch: ' + error);
    }
}
