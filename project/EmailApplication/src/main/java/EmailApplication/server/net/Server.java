/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EmailApplication.server.net;

import EmailApplication.constructors.Email;
import EmailApplication.constructors.User;
import java.io.StringReader;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

//WebSocket server endpoint. Here messeges are sent to from client.
@ServerEndpoint("/actions")
public class Server {

    @Inject
    private SessionHandler sessionHandler;

    //Nothing has to be done on server on open
    @OnOpen
    public void handleOpen(Session session) {
        System.out.println("Client connected..");
    }
    //Nothing has to be done on close, user stays online until logging out
    @OnClose
    public void handleClose(Session session) {
        System.out.println("Client disconnected..");
    }
    //Reports to user if an error occurs
    @OnError
    public void handleError(Throwable error, Session session) {
        sessionHandler.errorMsg(session);
    }
    //Handle messages sent from client
    @OnMessage
    public void handleMessage(String message, Session session) {
        Email newMail = null;
        Email sMail = null;
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            switch (jsonMessage.getString("type")) {
                case "login":
                    User user = new User(jsonMessage.getString("user"), session, jsonMessage.getString("password"));
                    sessionHandler.addUser(user);
                    break;
                case "relog":
                    User u = sessionHandler.getUser(jsonMessage.getString("user"), session);
                    sessionHandler.startupMails(u);
                    break;
                case "logout":
                    sessionHandler.removeUser(session);
                    break;

                case "register":
                    User newUser = new User(jsonMessage.getString("user"), session, jsonMessage.getString("password"));
                    sessionHandler.registerUser(newUser);
                    break;

                case "mail":
                    newMail = new Email(jsonMessage.getString("sender"), jsonMessage.getString("reciever"),
                            "Unread", "Recieved", jsonMessage.getString("subject"), jsonMessage.getString("message"));
                    sMail = new Email(jsonMessage.getString("sender"), jsonMessage.getString("reciever"),
                            "Unread", "Sent", jsonMessage.getString("subject"), jsonMessage.getString("message"));
                    sessionHandler.addMail(newMail, sMail);
                    break;
                case "toggle":
                    sessionHandler.toggleEmail(jsonMessage.getInt("id"), session);
                    break;

                case "remove":
                    sessionHandler.removeMail(jsonMessage.getInt("id"), session);
                    break;

                default:
                    break;

            }

        }
        System.out.println("Recieved from client " + message);
    }
}
