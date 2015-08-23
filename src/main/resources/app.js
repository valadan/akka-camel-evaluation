var akkaWs = new WebSocket("ws://localhost:9393/akka");

akkaWs.onopen = function (event) {
    for (var i = 0; i < 10; i++) {
        setTimeout(
            function () {
                akkaWs.send("Akka msg.");
            },
            i * 1000
        )
    }
};