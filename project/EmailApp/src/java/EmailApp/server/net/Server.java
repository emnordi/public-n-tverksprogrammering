/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EmailApp.server.net;

import EmailApp.both.Email;
import EmailApp.both.User;
import EmailApp.server.controller.Controller;
import java.io.StringReader;
import java.rmi.RemoteException;
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

/**
 *
 * @author Emil
 */
@ServerEndpoint("/actions")
public class Server {

    @Inject
    private SessionHandler sessionHandler;
    private Controller cont;

    @OnOpen
    public void handleOpen(Session session) {
        try {
            cont = new Controller();
        } catch (RemoteException re) {
            re.printStackTrace();
        }

        System.out.println("Client connected..");
    }

    @OnClose
    public void handleClose(Session session) {
        sessionHandler.removeUser(session);
        System.out.println("Client disconnected..");
    }

    @OnError
    public void handleError(Throwable error) {
        error.printStackTrace();
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        Email newMail = null;
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
            
            if (jsonMessage.getString("type").equals("login")) {
                User newUser = new User(jsonMessage.getString("user"), session, jsonMessage.getString("password"));
                sessionHandler.addUser(newUser);
            } else if (jsonMessage.getString("type").equals("register")) {
                User newUser = new User(jsonMessage.getString("user"), session, jsonMessage.getString("password"));
                sessionHandler.addUser(newUser);
                cont.registerUser(newUser);
            } else {
                newMail = new Email(jsonMessage.getString("sender"), jsonMessage.getString("reciever"),
                        "Unread", "Sent", jsonMessage.getString("subject"), jsonMessage.getString("message"));
                sessionHandler.addMail(newMail);
            }
        }
        System.out.println("Recieved from client " + message);
    }
}
