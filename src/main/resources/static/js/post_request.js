export async function postRequest(url, data, type, returnType) {
    let contentType;
    if(type === "plain text") {
        contentType = "text/plain";
    } else if(type === "json") {
        contentType = "application/json";
    }

    const response = await fetch(url, {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        mode: "cors", // no-cors, *cors, same-origin
        cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
        credentials: "same-origin", // include, *same-origin, omit
        headers: {
            "Content-Type": contentType,
        },
        redirect: "follow", // manual, *follow, error
        referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: data, // body data type must match "Content-Type" header
    });

    if(returnType === "text") {
        return response.text();
    } else {
        return response.json();
    }
}