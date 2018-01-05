var webSocket = new WebSocket("ws://localhost:8080/EmailApp/actions");
    localStorage.setItem("user_id", 2312313);
    console.log(localStorage.getItem("user_id"));
var messageArea = document.getElementById("messageArea");
var textMessage = document.getElementById("textMessage");
var userName = document.getElementById("userName");
var password = document.getElementById("password");

var reciever = document.getElementById("recipient");
var subject = document.getElementById("subject");

webSocket.onopen = function (message) {
    processOpen(message);
};
webSocket.onclose = function (message) {
    processClose(message);
};
webSocket.onerror = function (message) {
    processError(message);
};
webSocket.onmessage = function (message) {
    var email = JSON.parse(message.data);

    if (email.kind == "mail") {
        printer(email);
    } else {
        processMessage(email);
    }
};
function processOpen(message) {
    messageArea.value += "Connecting to server \n";
}

function login() {
    var login = {
        type: "login",
        user: userName.value,
        password: password.value
    };
    webSocket.send(JSON.stringify(login));
}
function register() {
    var register = {
        type: "register",
        user: userName.value,
        password: password.value
    };
    webSocket.send(JSON.stringify(register));
}
function sendMessage() {
    if (textMessage.value != "close") {
        var mail = {
            type: "mail",
            sender: userName.value,
            reciever: reciever.value,
            subject: subject.value,
            message: textMessage.value
        };
        webSocket.send(JSON.stringify(mail));

        messageArea.value += "Sending to serv: " + textMessage.value + "\n";
        textMessage.value = "";
    } else {
        
    }
}

function printer(email) {
    var recieved = document.getElementById("recieved");
    var emailDiv = document.createElement("div");

    emailDiv.setAttribute("id", "0");
    emailDiv.setAttribute("class", "email " + "email.type");
    recieved.appendChild(emailDiv);

    var from = document.createElement("span");
    from.setAttribute("class", "emailSR");
    from.innerHTML = "Sender: " + email.from;
    emailDiv.appendChild(from);

    emailDiv.appendChild(document.createElement("br"));

    var subject = document.createElement("span");
    subject.setAttribute("class", "emailSR");
    subject.innerHTML = "Subject: " + email.subject;
    emailDiv.appendChild(subject);

    emailDiv.appendChild(document.createElement("br"));

    var msg = document.createElement("span");
    msg.setAttribute("class", "emailSR");
    msg.innerHTML = "Message: " + email.message;
    emailDiv.appendChild(msg);
}

function processMessage(message) {
    messageArea.value += "From serv: " + message.message + "\n";
}

function processClose(message) {
    webSocket.send("client dcd");
    messageArea.value += "Server disconnecting.. \n";
    webSocket.close();
}
function processError(message) {
    messageArea.value += "error.. \n";
}