// Get the token value from the URL query parameter
const urlParams = new URLSearchParams(window.location.search);
const tokenValue = urlParams.get('token');

// Select the hidden input field and set its value to the token value
const tokenInput = document.getElementById('token_input');
tokenInput.value = tokenValue;

var password = document.getElementById('password');
var form = document.getElementById('reset_password');
var spanReset = document.getElementById('reset_status');
var linkLogin = document.getElementById('link_login');

form.addEventListener("submit", function(event) {
    event.preventDefault();
    if(!checkPasswordMatch()) {
        spanReset.style.color = 'red';
        spanReset.textContent = "Passwords do not match.";
    } else {
        spanReset.textContent = "Processing your request...";
        spanReset.style.color = "rgb(255, 147, 20)";
        const formData = new FormData(form);

        fetch(form.action, {
            method: form.method,
            body: formData,
        })
            .then((response) => {
                if(!response.ok) {
                    return response.text().then(
                        text => {
                            throw new Error(text);
                        }
                    );
                } else {
                    message = response.text().then(message => {
                        console.log(message);
                        spanReset.textContent = message;
                        spanReset.style.color = 'rgb(255, 147, 20)';
                        linkLogin.style.display = 'inline-block';
                    });

                    form.reset();
                    var allElements = form.elements;
                    for (var i = 0, l = allElements.length; i < l; ++i) {
                        allElements[i].disabled=true;
                    }
                }
            })
            .catch((error) => {
                console.log(error);
                spanReset.textContent = error;
                spanReset.style.color = 'red';
                linkLogin.style.display = 'none';
                form.reset();
            });
    }
});

function checkPasswordMatch() {
    const fieldConfirmPassword = document.getElementById("confirm");
    return (fieldConfirmPassword.value === password.value);
}