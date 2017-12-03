/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.client.view;

import fileCatalog.all.Fclient;
import fileCatalog.all.FileDTO;
import fileCatalog.all.Fserver;
import fileCatalog.all.UserCredentials;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 *
 * @author Emil
 */
public class View implements Runnable {

    private boolean readFromUser;
    private MngOut out = new MngOut();
    private String username;
    private Fserver serv;
    String[] fileAndString;
    String[] userAndPass;
    String[] fileFromAndTo;
    private final Fclient remoteObject;

    public View() throws RemoteException {
        remoteObject = new ConsoleOutput();
    }

    public void start() {
        if (readFromUser) {
            return;
        }
        readFromUser = true;
        new Thread(this).start();
        out.println("Welcome, enter <help>. Or login or register");

    }

    @Override
    public void run() {
        while (readFromUser) {
            try {
                Scanner sc = new Scanner(System.in);
                String s = sc.nextLine();
                String[] commands = s.split(" ", 2);
                switch (commands[0].toLowerCase()) {
                    case "help":
                        welcomeMsg();
                        break;
                    case "register":
                        boolean registered;
                        servlookup("127.0.0.1");
                        userAndPass = commands[1].split(" ", 2);
                        registered = serv.register(remoteObject, new UserCredentials(userAndPass[0], userAndPass[1]));
                        if (registered) {
                            username = userAndPass[0];
                        } else {
                            out.println("Name taken, please enter another");
                        }
                        break;
                    case "unregister":
                        if (username == null) {
                            out.println("You are not logged in");
                        } else {
                            serv.unregister(username);
                        }
                        break;
                    case "login":
                        servlookup("127.0.0.1");
                        userAndPass = commands[1].split(" ", 2);
                        boolean verified = serv.login(remoteObject, new UserCredentials(userAndPass[0], userAndPass[1]));
                        if (verified) {
                            username = userAndPass[0];
                            out.println("You are logged on: " + username);
                        } else {
                            out.println("Invalid username/password");
                        }
                        break;
                    case "logout":
                        serv.logout(username);
                        break;
                    case "notifyme":
                        if (serv.notifyaccess(commands[1], username)) {
                            out.println("You will be notified when " + commands[1] + " is updated");
                        } else {
                            out.println("You are not the owner of the file, cannot notify");
                        }
                        break;
                    case "createdir":
                        serv.createDir(commands[1], username);
                        out.println("Directory " + commands[1] + " created");
                        break;
                    case "deletedir":
                        serv.deleteDir(commands[1], username);
                        out.println("Directory " + commands[1] + " deleted");
                        break;
                    case "listfiles":
                        List<? extends FileDTO> allPubFiles = serv.listFiles(username);

                        final Object[][] table = new String[allPubFiles.size() + 1][];
                        String acc;
                        table[0] = new String[]{"Filename:", "Owner:", "Access:", "Size:"};
                        for (int i = 0; i < allPubFiles.size(); i++) {
                            FileDTO a = allPubFiles.get(i);
                            String siz = Integer.toString(a.getSize());
                            if (a.getAccess() == 1) {
                                acc = "Public";
                            } else {
                                acc = "Private";
                            }

                            table[i + 1] = new String[]{a.getFilename(), a.getUsername(), acc, siz};
                        }
                        for (final Object[] row : table) {
                            System.out.format("%-15s%-15s%-15s%-15s\n", row);
                        }
                        break;
                    case "deletefile":
                        boolean deleted;
                        deleted = serv.deleteFile(commands[1], username);
                        if (deleted) {
                            out.println("File deleted");
                        } else {
                            out.println("You do not have permission to delete this file");
                        }
                        break;
                    case "updatefileaccess":
                        boolean updatable = false;
                        fileAndString = commands[1].split(" ");
                        if (fileAndString[1].equalsIgnoreCase("private")) {
                            updatable = serv.updateFileAccess(fileAndString[0], 0, username, 0);
                        } else if (fileAndString[1].equalsIgnoreCase("public")) {
                            int write = Integer.parseInt(fileAndString[2]);
                            updatable = serv.updateFileAccess(fileAndString[0], 1, username, write);
                        } else {
                            out.println("File must be declared private or public");
                        }
                        if (updatable) {
                            out.println("File updated");
                        } else {
                            out.println("You have to be the owner of the file to update it");
                        }

                        break;
                    case "updatefilecontent":
                        fileAndString = commands[1].split(" ", 2);
                        boolean updated = serv.updateFileContent(fileAndString[0], fileAndString[1], username);
                        if (updated) {
                            out.println("File has been updated");
                        } else {
                            out.println("You do not have permission to update the file");
                        }

                        break;
                    case "downloadfile":
                        boolean downloaded;
                        downloaded = serv.downloadFile(commands[1], username);
                        if (downloaded) {
                            out.println("File downloaded");
                        } else {
                            out.println("You do not have permission to download this file");
                        }
                        break;
                    case "uploadfile":
                        boolean uploadable = true;
                        fileAndString = commands[1].split(" ");
                        Path dir = Paths.get("Files");
                        Path from = dir.resolve(Paths.get(fileAndString[0]));
                        byte[] data = Files.readAllBytes(from);
                        int filesize = data.length;
                        System.out.println(filesize);
                        if (fileAndString[1].equalsIgnoreCase("private")) {
                            uploadable = serv.uploadFile(data, fileAndString[0], 0, username, filesize, 0);
                        } else if (fileAndString[1].equalsIgnoreCase("public")) {
                            int write = Integer.parseInt(fileAndString[2]);
                            uploadable = serv.uploadFile(data, fileAndString[0], 1, username, filesize, write);
                        } else {
                            out.println("File must be declared private or public");
                        }
                        if (uploadable) {
                            out.println("File uploaded");
                        } else {
                            out.println("There is already a file with that name");
                        }

                        break;
                    default:
                        out.println("Wrong input");
                }
            } catch (Exception e) {
                out.println("Failed");
                e.printStackTrace();
            }
        }
    }

    private void servlookup(String host) throws NotBoundException, MalformedURLException, RemoteException {
        serv = (Fserver) Naming.lookup("//" + host + "/" + Fserver.RegName);
    }

    void welcomeMsg() {
        out.println("(register) When registering, enter <username> <password>");
        out.println("(login) When logging in, enter <username> <password>");
        out.println("(logout) Logs you out");
        out.println("(listfiles) lists all public files and your own files");
        out.println("(notifyme) when requesting notifications for fileaccess enter <filename>");
        out.println("(uploadfile) When uploading file enter <filename> <access>(public, private) <writable>(0/1)");
        out.println("(downloadfile) when downloading a file enter <filepath>");
        out.println("(updatefileaccess) when changing  a file enter <filename> <access>(public, private) <writable>(0/1)");
        out.println("(updatefilecontent) when updating a files contents enter <filename> <content>");
        out.println("(deletefile) when deleting a file enter <filename>");
         

    }

    private class ConsoleOutput extends UnicastRemoteObject implements Fclient {

        public ConsoleOutput() throws RemoteException {
        }

        @Override
        public void getMsg(String msg) {
            out.println((String) msg);
        }
    }
}
