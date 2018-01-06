var webSocket = new WebSocket("ws://localhost:8080/EmailApplication/actions");
console.log(localStorage.getItem("user"));
var messageArea = document.getElementById("messageArea");
var textMessage = document.getElementById("textMessage");
var userName = document.getElementById("userName");
var password = document.getElementById("password");

var reciever = document.getElementById("recipient");
var subject = document.getElementById("subject");

webSocket.onopen = function (message) {
    if(localStorage.getItem("user") != null){
        hideLoginForm();
        loginMsg();
        var login = {
        type: "relog",
        user: localStorage.getItem("user")
    };
    webSocket.send(JSON.stringify(login)); 
    }else{
    hideMailForm();
    hideSRForm();
    }
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
        hideLoginForm();
        showMailForm();
        showSRForm();
        printer(email);
    } else if(email.kind == "Login"){
        localStorage.setItem("user", email.user.toString());
        loginMsg();
        processMessage(email);
    } else{
        
    }
};

function loginMsg() {

    var content = document.getElementById("userMenu");
    var userDiv = document.createElement("div");
    userDiv.setAttribute("id", "usermenu");
    userDiv.setAttribute("class", "usm");
    content.appendChild(userDiv);
    
    var title = document.createElement("span");
    title.setAttribute("class", "welcomeTitle");
    title.innerHTML = "Welcome: " + localStorage.getItem("user") + "!";
    userDiv.appendChild(title);
    userDiv.appendChild(document.createElement("br"));
    
    var logout = document.createElement("button");
    logout.setAttribute("class", "logoff");
    logout.innerHTML = "<a href=\"#\" OnClick=logoutFromApp(" + localStorage.getItem("user") + ")>Logout</a>";
    userDiv.appendChild(logout);
    
    }
function successMessage(message){    
    var ab = document.getElementById("alerts");
    var msg = document.createElement("span");
    msg.setAttribute("id", "alertbo");
    msg.setAttribute("class", "alert-box success");
    msg.innerHTML = message;
    ab.appendChild(msg);
    
    setTimeout(function () {
        document.getElementById('alertbo').style.display='none';
    }, 10000);
};


function toggleMail(id) {
    var toggleMail = {
        type: "toggle",
        id: id
    };
    webSocket.send(JSON.stringify(toggleMail));
    document.getElementById(id).remove();
}

function login() {
  var f = document.getElementsByTagName('input')[0];
  if(f.checkValidity()) {
    var login = {
        type: "login",
        user: userName.value,
        password: password.value
    };
    webSocket.send(JSON.stringify(login));
  } else {
    alert("Please fill in the required username and password fields");
  }
    
}

function logoutFromApp(email){
    var logout = {
        type: "logout",
        user: localStorage.getItem("user")
    };
    webSocket.send(JSON.stringify(logout));
    localStorage.removeItem("user");
    hideMailForm();
    hideSRForm();
    showLoginForm();
    
}
function register() {
    var f = document.getElementsByTagName('input')[0];
  if(f.checkValidity()) {
    var register = {
        type: "register",
        user: userName.value,
        password: password.value
    };
    webSocket.send(JSON.stringify(register));
  }else{
      alert("Please fill in the required username and password fields");
  }
}
function sendMessage() {
        var mail = {
            type: "mail",
            sender: localStorage.getItem("user"),
            reciever: reciever.value,
            subject: subject.value,
            message: textMessage.value
        };
        webSocket.send(JSON.stringify(mail));
        messageArea.value += "Sending to serv: " + textMessage.value + "\n";
        textMessage.value = "";
        subject.value = "";
        reciever.value = "";
         successMessage("Email Sent sucessfully!");

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

function hideLoginForm(){
    document.getElementById("loginForm").style.display = 'none';
}
function showLoginForm(){
    document.getElementById("loginForm").style.display = '';
}
function hideMailForm(){
    document.getElementById("mailForm").style.display = 'none';
}
function showMailForm(){
    document.getElementById("mailForm").style.display = '';
}
function hideSRForm(){
    document.getElementById("Recieved").style.display = 'none';
    document.getElementById("Sent").style.display = 'none';
}
function showSRForm(){
    document.getElementById("Recieved").style.display = '';
    document.getElementById("Sent").style.display = '';
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