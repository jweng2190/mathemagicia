// Create a search bar element
var searchBox = document.createElement("input");
searchBox.type = "text";
searchBox.placeholder = "Enter a full name";
searchBox.style.display = "none";

var button = document.getElementById("invite");
var table = document.getElementById("myTable");

document.body.appendChild(searchBox);

button.addEventListener('click', displaySearch);

var emailData = [];
searchBox.addEventListener("keyup", function (event) {
    if (event.key === "Enter") {
        //delete rows
        while(table.rows.length > 0) {
            table.deleteRow(0);
        }

        var inputValue = searchBox.value;
        searchBox.value = '';
        searchUsers("/search", inputValue).then((data) => {
            console.log(data);
            emailData = data;

            for (var i = 0; i < emailData.length; i++) {
                var row = table.insertRow(i);
                var cell = row.insertCell(0);
                cell.innerHTML = emailData[0];
            }
        });
    }
});


function displaySearch() {
    if (searchBox.style.display === "none") {
        searchBox.style.display = "block";
    } else {
        searchBox.style.display = "none";
    }
}

async function searchUsers(url = "", data = {}) {
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
