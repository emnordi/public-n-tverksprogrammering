var webSocket = new WebSocket("ws://localhost:8080/EmailApplication/actions");
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

function toggleMail(id) {
    var toggleMail = {
        type: "toggle",
        id: id
    };
    webSocket.send(JSON.stringify(toggleMail));
    document.getElementById(id).remove();
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
        subject.value = "";
        reciever.value = "";

}
function deleteMail(message) {
    var id = message;
    var remove = {
        type: "remove",
        id: id 
    };
    document.getElementById(id).remove();
    webSocket.send(JSON.stringify(remove));
}
function printer(email) {

    var recieved = document.getElementById(email.type);
    var emailDiv = document.createElement("div");
    emailDiv.setAttribute("id", email.id);
    emailDiv.setAttribute("class", "email " + email.type);
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
    emailDiv.appendChild(document.createElement("br"));
    
    var status = document.createElement("span");
    status.setAttribute("class", "stat");
    if (email.status === "Unread") {
        status.innerHTML = email.status + " (<a href=\"#\" OnClick=toggleMail(" + email.id + ")>Mark as read</a>)";
    } else if (email.status === "Read") {
        status.innerHTML = email.status + " (<a href=\"#\" OnClick=toggleMail(" + email.id + ")>Mark as unread</a>)";
    }
    emailDiv.appendChild(status);
    emailDiv.appendChild(document.createElement("br"));
    
    var delMail = document.createElement("span");
    delMail.innerHTML = "(<a href=\"#\" OnClick=deleteMail(" + email.id + ") ><b>Delete email</b></a>)";
    emailDiv.appendChild(delMail);
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