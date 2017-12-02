/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.server.model;

import fileCatalog.all.FileDTO;

/**
 *
 * @author Emil
 */
public class UserFile implements FileDTO{
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
    @Override
    public String getFilename() {
        return filename;
    }
    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public int getAccess() {
        return access;
    }
    @Override
    public int getSize() {
        return size;
    }
    
}
