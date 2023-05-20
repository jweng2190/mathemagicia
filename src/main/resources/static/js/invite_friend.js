var searchBox = document.getElementById("search_box");
var button = document.getElementById("search");
var inviteButton = document.getElementById("invite");
var searchIcon = document.getElementById("search_icon");
var form = document.getElementById("friend_list");
var inputs = form.getElementsByTagName("input");
var labels = form.getElementsByTagName("label");
var brList = form.getElementsByTagName("br");

button.addEventListener("click", displaySearch);

var emailData = [];
searchBox.addEventListener("keyup", function (event) {
    if (event.key === "Enter") {
        var inputValue = searchBox.value;
        searchBox.value = '';
        clearForm();

        postRequest("/search", inputValue).then((data) => {
            console.log(data);
            emailData = data;

            for (var i = 0; i < emailData.length; i++) {
                var input = document.createElement("input");
                input.type = "radio";
                input.id = i;
                input.name = "friend";
                var label = document.createElement("label");
                label.for = i;
                label.textContent = inputValue + "\t" +  emailData[i];
                form.appendChild(input);
                form.insertBefore(input, inviteButton);
                form.appendChild(label);
                form.insertBefore(label, inviteButton);
                var br = document.createElement("br");
                br.id = i;
                form.insertBefore(br, inviteButton);
            }
        });
    }
});

searchIcon.addEventListener("click", function () {
    var N = inputs.length;
    var inputValue = searchBox.value;
    searchBox.value = '';
    for(var i = 0; i < N - 1; i++) {
        inputs[0].remove();
        labels[0].remove();
        brList[0].remove();
    }

    postRequest("/search", inputValue).then((data) => {
        console.log(data);
        emailData = data;

        for (var i = 0; i < emailData.length; i++) {
            var input = document.createElement("input");
            input.type = "radio";
            input.id = i;
            input.name = "friend";
            var label = document.createElement("label");
            label.for = i;
            label.textContent = inputValue + "\t" +  emailData[i];
            form.appendChild(input);
            form.insertBefore(input, inviteButton);
            form.appendChild(label);
            form.insertBefore(label, inviteButton);
            var br = document.createElement("br");
            br.id = i;
            form.insertBefore(br, inviteButton);
        }
    });
});

form.addEventListener("submit", function (event) {
    event.preventDefault();

    var radios = document.getElementsByName("friend");

    for (var i = 0, length = radios.length; i < length; i++) {
        if (radios[i].checked) {
            var nameAndEmail = labels[i].innerHTML;
            var email = nameAndEmail.split("\t")[1];
            postRequest("/email", email).then((response) => {
                msg = response[0]
                console.log(msg);
                alert(msg);
            });
            break;
        }
    }
})


function displaySearch() {
    if (searchBox.style.display === "none") {
        searchBox.style.display = "block";
    } else {
        searchBox.style.display = "none";
    }

    if (inviteButton.style.display === "none") {
        inviteButton.style.display = "block";
    } else {
        inviteButton.style.display = "none";
    }

    if (searchIcon.style.display === "none") {
        searchIcon.style.display = "block";
    } else {
        searchIcon.style.display = "none";
    }

    clearForm();
}

function clearForm() {
    var N = inputs.length;

    for(var i = 0; i < N - 1; i++) {
        inputs[0].remove();
        labels[0].remove();
        brList[0].remove();
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
