/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EmailApplication.constructors;

import javax.websocket.Session;

/**
 *
 * @author Emil
 */
public class User {
    
    private String username;
    private Session session;
    private String password;
    
    public User(){   
    }
    
    public User(String username, Session session, String password){
        this.username = username;
        this.session = session;
        this.password = password;
    }
    
    public String getUsername(){
        return username;
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    public String getPassword(){
        return password;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public Session getSession(){
        return session;
    }
    
    public void setSession(Session session){
        this.session = session;
    }
}
