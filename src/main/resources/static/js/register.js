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
    
    if(response.ok) {
        window.location.href = "/register_success";
    } else {
        window.location.href = "/register_fail";
    }
}

