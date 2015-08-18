var exampleSocket = new WebSocket("ws://localhost:9292/echo");
exampleSocket.onopen = function (event) {
    exampleSocket.send("Here's some text that the server is urgently awaiting!");
};
exampleSocket.onmessage = function (event) {
        console.log("ddd: " + event)
};