/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.server.model;

/**
 *
 * @author Emil
 */
public class UserFile {
    private final String filename;
    private final String username;
    private final int access;
    private final int size;
    
    
    public UserFile(String filename, String username, int access, int size) {
        this.username = username;
        this.filename = filename;
        this.access = access;
        this.size = size;
    }
    public String getFilename() {
        return filename;
    }
    public String getUsername() {
        return username;
    }
    public int getAccess() {
        return access;
    }
    public int getSize() {
        return size;
    }
    
}
