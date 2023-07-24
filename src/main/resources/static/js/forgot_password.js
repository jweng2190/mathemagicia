var paragraphEmail = document.getElementById('sent_email_display');
var messageStatus = document.getElementById('message_status');
var form = document.getElementById('forgot_password');
var inputEmail = document.getElementById('input_email');

form.addEventListener("submit", function(event) {
    event.preventDefault();
    messageStatus.textContent = "Processing your request...";
    messageStatus.style.color = "rgb(255, 147, 20)";
    paragraphEmail.style.visibility = 'visible';
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
                    messageStatus.textContent = message;
                });
            }
        })
        .catch((error) => {
            console.log(error);
            messageStatus.textContent = error;
            messageStatus.style.color = 'rgb(255, 0, 0)';
        });

    // Clear the form after submitting
    form.reset();
})