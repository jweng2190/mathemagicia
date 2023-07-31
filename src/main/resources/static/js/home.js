const url = "http://localhost:8080";
var topBarUsername = document.getElementById("topbar_username");
getUsername().then((username) => {
    topBarUsername.textContent = username; 
});

async function getUsername() {
    const response = await fetch(url + '/username');
    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }
    const username = await response.text();
    return username;
}