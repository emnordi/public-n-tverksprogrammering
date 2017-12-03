/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.server.integration;

import fileCatalog.server.model.UserFile;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Emil
 */
public class FileDAO {

    private static final String UTABLE = "USERS";
    private static final String FTABLE = "FILES";
    private PreparedStatement createUserSm;
    private PreparedStatement findUserSm;
    private PreparedStatement deleteUserSm;
    private PreparedStatement changeAccessSm;
    private PreparedStatement addFileSm;
    private PreparedStatement getAccessSm;
    private PreparedStatement deleteFileSm;
    private PreparedStatement changeWritableSm;
    private PreparedStatement updateSizeSm;
    private PreparedStatement getAllFilesSm;

    public FileDAO() {
        try {
            Connection connection = createDatasource();
            prepareStatements(connection);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection createDatasource() throws ClassNotFoundException, SQLException {
        Connection connection = databaseCon();
        if (!tableExists(connection, UTABLE) && !tableExists(connection, FTABLE)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("create table " + UTABLE + "(username varchar(50) primary key, password varchar(50))");
            statement.executeUpdate("create table " + FTABLE + "(filename varchar(50) primary key, username varchar(50), access int, size int, writable int)");
        }
        return connection;
    }

    public boolean registerUser(String username, String password) {
        try {
            createUserSm.setString(1, username);
            createUserSm.setString(2, password);
            int rows = createUserSm.executeUpdate();
            if (rows != 1) {
                return false;
            }
            return true;
        } catch (SQLException sqle) {
            return false;
        }
    }

    public boolean authenticate(String username, String password) {
        try {
            findUserSm.setString(1, username);
            findUserSm.setString(2, password);
            ResultSet user = findUserSm.executeQuery();
            user.next();
            if (user.getString(1).equals(username)) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException sqle) {
            return false;
        }
    }

    public void unregisterUser(String username) {
        try {
            deleteUserSm.setString(1, username);
            deleteUserSm.executeUpdate();

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public boolean uploadFile(String filename, int access, String username, int filesize, int writable) {
        try {
            addFileSm.setString(1, filename);
            addFileSm.setString(2, username);
            addFileSm.setInt(3, access);
            addFileSm.setInt(4, filesize);
            addFileSm.setInt(5, writable);
            addFileSm.executeUpdate();
            return true;
        } catch (SQLException sqle) {
            return false;
        }
    }

    //Checks if user has access to downloading file and then downloads
    public boolean downloadFile(String filename, String usernam) {
        try {
            int acc = -1;
            ResultSet result = null;
            getAccessSm.setString(1, filename);
            result = getAccessSm.executeQuery();
            if (result.next()) {
                acc = result.getInt("ACCESS");
                String user = result.getString("USERNAME");

                switch (acc) {
                    case 1:
                        return true;
                    case 0:
                        if (user.equals(usernam)) {
                            return true;
                        } else {
                            return false;
                        }
                    default:
                        return false;
                }
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean updateFileContent(String filename, String usernam) {
        try {
            int acc = -1;
            int writ = -1;
            ResultSet result = null;
            getAccessSm.setString(1, filename);
            result = getAccessSm.executeQuery();
            if (result.next()) {
                acc = result.getInt("ACCESS");
                writ = result.getInt("WRITABLE");
                String user = result.getString("USERNAME");

                switch (acc) {
                    case 1:
                        if (writ == 1 || user.equals(usernam)) {
                            return true;
                        } else {
                            return false;
                        }
                    case 0:
                        if (user.equals(usernam)) {
                            return true;
                        } else {
                            return false;
                        }
                    default:
                        return false;
                }
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean updateFileAccess(String filename, int access, String usernam, int write) {
        try {
            ResultSet result = null;
            getAccessSm.setString(1, filename);
            result = getAccessSm.executeQuery();
            if (result.next()) {
                String user = result.getString("USERNAME");
                if (user.equals(usernam)) {
                    changeAccessSm.setInt(1, access);
                    changeAccessSm.setString(2, filename);
                    changeAccessSm.executeUpdate();
                    changeWritableSm.setInt(1, write);
                    changeWritableSm.setString(2, filename);
                    changeWritableSm.executeUpdate();
                    return true;
                }

            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
        return false;
    }

    public void updateSize(String filenam, int size) throws SQLException {
        updateSizeSm.setInt(1, size);
        updateSizeSm.setString(2, filenam);
        updateSizeSm.executeUpdate();
    }

    public boolean checkowner(String file, String user) {
        ResultSet result = null;
        try {
            getAccessSm.setString(1, file);
            result = getAccessSm.executeQuery();
            if (result.next()) {
                if (result.getString("USERNAME").equals(user)) {
                    return true;
                }
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
        return false;
    }

    public List<UserFile> listFiles(String username) {
        List<UserFile> filelist = new ArrayList<>();
        UserFile file;
        ResultSet result = null;
        try {
            getAllFilesSm.setString(1, username);
            getAllFilesSm.setInt(2, 1);
            result = getAllFilesSm.executeQuery();
            while (result.next()) {
                file = new UserFile(result.getString("FILENAME"), result.getString("USERNAME"), result.getInt("ACCESS"), result.getInt("SIZE"));
                filelist.add(file);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return filelist;
    }

    //Checks if the user has access to deleting the file and deletes
    public boolean deleteFile(String filename, String usernam) {
        try {
            int acc = -1;
            int writ = -1;
            ResultSet result = null;
            getAccessSm.setString(1, filename);
            result = getAccessSm.executeQuery();
            if (result.next()) {
                acc = result.getInt("ACCESS");
                writ = result.getInt("WRITABLE");
                String user = result.getString("USERNAME");

                switch (acc) {
                    case 1:
                        if (writ == 1 || user.equals(usernam)) {
                            deleteFileSm.setString(1, filename);
                            deleteFileSm.executeUpdate();
                            return true;
                        } else {
                            return false;
                        }
                    case 0:
                        if (user.equals(usernam)) {
                            deleteFileSm.setString(1, filename);
                            deleteFileSm.executeUpdate();
                            return true;
                        } else {
                            return false;
                        }
                    default:
                        return false;
                }
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean tableExists(Connection connection, String table) throws SQLException {
        //Name is in third column
        int col = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        try (ResultSet rs = dbm.getTables(null, null, null, null)) {
            for (; rs.next();) {
                if (rs.getString(col).equals(table)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void prepareStatements(Connection connection) throws SQLException {
        createUserSm = connection.prepareStatement("INSERT INTO "
                + UTABLE + " VALUES (?, ?)");
        findUserSm = connection.prepareStatement("SELECT * from "
                + UTABLE + " WHERE (username = ? AND password = ?)");
        deleteUserSm = connection.prepareStatement("DELETE FROM "
                + UTABLE
                + " WHERE username = ?");
        addFileSm = connection.prepareStatement("INSERT INTO "
                + FTABLE + " VALUES (?, ?, ?, ?, ?)");
        deleteFileSm = connection.prepareStatement("DELETE FROM "
                + FTABLE
                + " WHERE filename = ?");
        getAccessSm = connection.prepareStatement("SELECT * from "
                + FTABLE + " WHERE filename = ? ");
        changeAccessSm = connection.prepareStatement("UPDATE "
                + FTABLE + " SET access = ? WHERE filename = ? ");
        changeWritableSm = connection.prepareStatement("UPDATE "
                + FTABLE + " SET writable = ? WHERE filename = ? ");
        getAllFilesSm = connection.prepareStatement("SELECT * from "
                + FTABLE + " WHERE (username = ? OR access = ?) ");
        updateSizeSm = connection.prepareStatement("UPDATE "
                + FTABLE + " SET size = ? WHERE filename = ? ");
    }

    private Connection databaseCon() throws ClassNotFoundException, SQLException {
        //Loads to jvm
        Class.forName("org.apache.derby.jdbc.ClientXADataSource");
        return DriverManager.getConnection("jdbc:derby://localhost:1527/FileCatalog", "emil", "emil");
    }
}
