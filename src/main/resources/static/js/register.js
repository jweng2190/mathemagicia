const formRegister = document.getElementById("register_form");
formRegister.addEventListener("submit", function(event) {
    event.preventDefault();
    sendFormData();
});

async function sendFormData() {
    const formData = new FormData(formRegister);
    const urlEncodedData = new URLSearchParams(formData);

    const response = await fetch("/register", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: urlEncodedData
    });
    console.log(response);

    const message = await response.text();
    
    if(response.ok) {
        window.location.href = "/register_success";
    } else {
        if(message === "username existing") {
            console.log("username existing");
        } else if(message === "email existing") {
            console.log("email existing");
        }
        //window.location.href = "/register_fail";
    }
}

