package EmailApplication.server.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class EmailData implements Serializable {

    @Id
    @GeneratedValue
    private int eid;
    private String sender;
    //Person recieving mail
    private String reciever;
    //Read or Unread
    private String status;
    //Sent or recieved
    private String type;

    private String subject;
    private String message;

    //Create instance of EmilData
    public EmailData() {
    }

    public EmailData(String sender, String reciever, String status, String type, String subject, String message) {
        this.sender = sender;
        this.reciever = reciever;
        this.status = status;
        this.type = type;
        this.subject = subject;
        this.message = message;
    }
    public EmailData(int eid, String sender, String reciever, String status, String type, String subject, String message) {
        this.eid = eid;
        this.sender = sender;
        this.reciever = reciever;
        this.status = status;
        this.type = type;
        this.subject = subject;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getReciever() {
        return reciever;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public int getEid() {
        return eid;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }
}
