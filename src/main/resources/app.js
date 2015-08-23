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

var camelWs = new WebSocket("ws://localhost:9292/camel");

camelWs.onopen = function (event) {
    for (var i = 0; i < 10; i++) {
        setTimeout(
            function () {
                camelWs.send("Camel msg.");
            },
            i * 1000
        )
    }
};