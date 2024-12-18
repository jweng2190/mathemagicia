async function verifyUser() {
    try {
        const response = await fetch('/verify', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ code: 'your-verification-code' })
        });

        if (!response.ok) {
            throw new Error(`Server error: ${response.statusText}`);
        }

        const html = await response.text();
        document.body.innerHTML = html; // Display the response HTML
    } catch (error) {
        console.error('Error:', error);
        document.body.innerHTML = `<h1>Verification failed. Please try again later.</h1>`;
    }
}

verifyUser();