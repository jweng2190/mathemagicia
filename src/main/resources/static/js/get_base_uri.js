export function getBaseUri() {
    var loc = window.location, base_uri;
    if (loc.protocol === "https:") {
        base_uri = "wss:";
    } else {
        base_uri = "ws:";
    }
    base_uri += "//" + loc.host;
    return base_uri;
}