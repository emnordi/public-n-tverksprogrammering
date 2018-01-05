package EmailApplication.server.net;

import EmailApplication.both.Email;
import EmailApplication.both.User;
import EmailApplication.server.controller.Controller;
import EmailApplication.server.model.EmailData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

@ApplicationScoped
public class SessionHandler {

    @Inject
    private Controller cont;

    private int emailId = 0;
    //Stores active session
    private final Set<User> users = new HashSet<>();
    private final Set<String> msgs = new HashSet<>();
    //Stores emails until db is setup
    private final Set<Email> emails = new HashSet<>();
    private Session reciever;
    private Session sender;
    private EmailData ed;
    private EmailData eds;
    private Set<EmailData> rmails;
    private Set<EmailData> smails;

//Add user
    public void addUser(User newUser) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject msg;
        if (cont.authenticateUser(newUser)) {
            users.add(newUser);
            msg = provider.createObjectBuilder()
                    .add("kind", "msg")
                    .add("message", "Logged on").build();
            
            startupMails(newUser);
        } else {
            msg = provider.createObjectBuilder()
                    .add("kind", "msg")
                    .add("message", "Invalid credentials, try again!").build();
        }
        sendToOneSession(newUser.getSession(), msg);
    }

    public void registerUser(User newUser) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject msg;
        if (cont.registerUser(newUser)) {
            users.add(newUser);
            msg = provider.createObjectBuilder()
                    .add("kind", "msg")
                    .add("message", "You are registered!").build();
        } else {
            msg = provider.createObjectBuilder()
                    .add("kind", "msg")
                    .add("message", "That username is taken, please pick another!").build();
        }
        sendToOneSession(newUser.getSession(), msg);
    }

    public void startupMails(User username) {
        rmails = cont.getRecieved(username.getUsername());
        smails = cont.getSent(username.getUsername());
        if (!rmails.isEmpty()) {
            for (EmailData mail : rmails) {
                JsonObject addMessage = createMessage(mail);
                System.out.println(addMessage.toString());
                sendToOneSession(username.getSession(), addMessage);
            }
        }
        if (!smails.isEmpty()) {
            for (EmailData mail : smails) {
                JsonObject addMessage = createMessage(mail);
                sendToOneSession(username.getSession(), addMessage);
            }
        }
    }

    public void removeUser(Session session) {
        for (User user : users) {
            if (user.getSession().equals(session)) {
                users.remove(user);
                System.out.println("User removed");
            }
        }
    }

    public void toggleEmail(int id) {
        JsonProvider provider = JsonProvider.provider();
        Device device = getDeviceById(id);
        if (device != null) {
            if ("On".equals(device.getStatus())) {
                device.setStatus("Off");
            } else {
                device.setStatus("On");
            }
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "toggle")
                    .add("id", device.getId())
                    .add("status", device.getStatus())
                    .build();
            sendToAllConnectedSessions(updateDevMessage);
        }
    }
    
    private void sendToOneSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void removeMail(int id) {
        cont.removeMail(id);
    }

    public void addMail(Email email, Email sMail) {
        emails.add(email);
        ed = cont.addMail(email);
        eds = cont.addMail(sMail);
        emailId++;
        for (User user : users) {
            if (user.getUsername().equals(email.getReciever())) {
                reciever = user.getSession();
                JsonObject addMessage = createMessage(ed);
                sendToOneSession(reciever, addMessage);
            }
            if (user.getUsername().equals(email.getSender())) {
                sender = user.getSession();
                JsonObject addMessage = createMessage(eds);
                sendToOneSession(sender, addMessage);
            }
//hello piglet. i love you
        }
    }

    private JsonObject createMessage(EmailData mail) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("kind", "mail")
                .add("id", mail.getEid())
                .add("to", mail.getReciever())
                .add("from", mail.getSender())
                .add("type", mail.getType())
                .add("status", mail.getStatus())
                .add("subject", mail.getSubject())
                .add("message", mail.getMessage())
                .build();
        return addMessage;
    }

}
