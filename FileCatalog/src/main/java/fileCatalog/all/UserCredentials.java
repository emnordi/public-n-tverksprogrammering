/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.all;

import java.io.Serializable;

/**
 *
 * @author Emil
 */
public class UserCredentials implements Serializable {
    private final String username;
    private final String password;

    //Create instance
    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    //Return password and username
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
}
