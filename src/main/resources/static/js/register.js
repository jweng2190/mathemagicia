const formRegister = document.getElementById("register_form");
const loadingDiv = document.getElementsByClassName("loader")[0];
const registerSpan = document.getElementById("register_span");
const statusRegister = document.getElementById("status_register");

formRegister.addEventListener("submit", function(event) {
    event.preventDefault();

    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('password-confirm');

    if(password.value !== confirmPassword.value) {
        invalidMsg("passwords different");
    } else {
        sendFormData();
    }
});

async function sendFormData() {
    loadingDiv.style.display = "inline-block";
    registerSpan.textContent = '';
    statusRegister.textContent = "";
    statusRegister.style.display = "none";

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

    loadingDiv.style.display = "none";
    registerSpan.textContent = "Register";
    if(response.ok) {
        window.location.href = "/register_success";
    } else {
        invalidMsg(message);
    }
}

function invalidMsg(msg) {
    if(msg === "username existing") {
        statusRegister.textContent = "Username already in use. Please provide another username.";
    } else if(msg === "email existing") {
        statusRegister.textContent = "Email already in use. Please provide another email.";
    } else if(msg === "passwords different") {
        statusRegister.textContent = "Passwords do not match.";
    }
    statusRegister.style.color = "#cc0000";
    statusRegister.style.display = "";
}

