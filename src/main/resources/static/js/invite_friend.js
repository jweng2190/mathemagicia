var searchBox = document.getElementById("search_box");
var searchButton = document.getElementById("search_button");
var form = document.getElementById("invite_form");
var table = document.getElementById("friend_table");

var activeButtonId = null;

searchButton.addEventListener("click", processSearch);

var emailData = [];

searchBox.addEventListener("keydown", function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        processSearch();
    }
});


function processSearch() {
    var inputValue = searchBox.value;
    searchBox.value = '';

    clearTable();

    postRequest("/search", inputValue).then((data) => {
        console.log(data);
        emailData = data;

        displaySearch(emailData);
    });
}


function displaySearch(emailData) {
    for(var i = 0; i < emailData.length; i++) {
        var row = table.insertRow(i);
        var cellRadio = row.insertCell(0);
        var cellUsername = row.insertCell(1);
        var cellEmail = row.insertCell(2);
        var cellInvite = row.insertCell(3);

        cellRadio.style.width = "7%";
        cellUsername.style.width = "36%";
        cellEmail.style.width = "36%";
        cellInvite.style.width = "21%";

        var checkbox = document.createElement("input");
        checkbox.type = "checkbox"
        checkbox.id = "checkbox" + i;
        checkbox.addEventListener("click", function(event) {
            var buttonId = event.target.id;
            enableButton(buttonId);
        });

        cellRadio.appendChild(checkbox);

        cellUsername.innerHTML = emailData[i][0];
        cellEmail.innerHTML = emailData[i][1];

        var inviteButton = document.createElement("button");
        inviteButton.className = "btn btn-primary";
        inviteButton.disabled = true;
        inviteButton.innerHTML = "Invite";
        inviteButton.id = "invite" + i;
        inviteButton.addEventListener("click", emailPlayer);
        cellInvite.appendChild(inviteButton);

        if(table.style.display === "none") {
            table.style.display = "table";
        }
    }
}

function enableButton(buttonId) {
    let buttonIndex = parseInt(buttonId.replace("checkbox", ""), 10);

    if(activeButtonId !== null) {
        if(activeButtonId === buttonIndex) {
            var checkbox = document.getElementById("checkbox" + activeButtonId);
            document.getElementById("invite" + activeButtonId).disabled = !(checkbox.checked);
            return;
        }
        document.getElementById("checkbox" + activeButtonId).checked = false;
        document.getElementById("invite" + activeButtonId).disabled = true;
    }

    
    activeButtonId = buttonIndex;
    document.getElementById("checkbox" + activeButtonId).checked = true;
    document.getElementById("invite" + activeButtonId).disabled = false;
}

function emailPlayer() {
    console.log("Emailed Player");
    var email = table.rows[activeButtonId].cells[2].innerHTML;
    postRequest("/email", email).then((msg) => {
        alert(msg);
    });

    var gameId = sessionStorage.getItem("gameId");
    window.location.href = "/game/" + gameId;
}

function clearTable() {
    while (table.rows.length > 0) {
        table.deleteRow(0);
    }
}

async function postRequest(url = "", data = {}) {
    const response = await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        mode: "cors", // no-cors, *cors, same-origin
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        credentials: "same-origin", // include, *same-origin, omit
        headers: {
            "Content-Type": "text/plain",
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: data, // body data type must match "Content-Type" header
    });
    return response.json();
}
