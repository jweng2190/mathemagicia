const verificationForm = document.getElementById('verification_form');
const resendCodeButton = document.getElementById('resend_code');
const resendMessage = document.getElementById('resend_msg');

const statusMsg = document.getElementById('status_msg');
const loginButton = document.getElementById('register_login');

// Handle form submission for verification
verificationForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    statusMsg.style.display = "none";
    loginButton.style.display = "none";

    const code = document.getElementById('verification_code').value;
    const response = await fetch('/verify?code=' + code);

    if (!response.ok) {
        statusMsg.style.color = "#ff2c2c";
        statusMsg.textContent = "Your account could not be verified. Please try again later.";
    } else {
        disableForm();
        statusMsg.style.color = "#63e663";
        statusMsg.textContent = "Your account has been successfully verified.";
        loginButton.style.display = "";
    }

    statusMsg.style.display = "";
});

resendCodeButton.addEventListener('click', async() => {
    statusMsg.textContent = "Verification code resent to your email.";
    statusMsg.style.color = "rgb(255, 235, 16)";
    statusMsg.style.display = "";
});

const disableForm = () => {
    const verificationInput = document.getElementById('verification_code');
    const verifyButton = document.getElementById('verify_button');
    verificationInput.value = "";
    verificationInput.readOnly = true;
    verifyButton.disabled = true;
    verifyButton.style.display = "none";

    resendCodeButton.style.display = "none";
}