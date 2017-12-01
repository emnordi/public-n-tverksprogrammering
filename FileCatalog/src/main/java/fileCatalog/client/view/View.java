/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.client.view;
import fileCatalog.all.Fclient;
import fileCatalog.all.Fserver;
import fileCatalog.all.UserCredentials;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.function.Consumer;
/**
 *
 * @author Emil
 */
public class View implements Runnable{
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
        out.println("Welcome, make an action");
    }
    
    @Override
    public void run() {
        while (readFromUser) {
            try {
            Scanner sc = new Scanner(System.in);
            String s = sc.nextLine();
            String[] commands = s.split(" ", 2);
                switch (commands[0].toLowerCase()) {
                    case "quit":
                        readFromUser = false;
                        break;
                    case "register":
                        servlookup("127.0.0.1");
                        userAndPass = commands[1].split(" ", 2);
                        username = serv.register(remoteObject, new UserCredentials(userAndPass[0], userAndPass[1]));
                        out.println(username);
                        if(username.equals("UserAlreadyExists")){
                            out.println("Name taken, please enter another");
                        }
                        break;
                    case "unregister":
                        if(username == null){
                            out.println("You are not logged in");
                        }else{
                            serv.unregister(username);
                        }
                        break;
                    case "login":
                        servlookup("127.0.0.1");
                        userAndPass = commands[1].split(" ", 2);
                        boolean verified = serv.login(remoteObject, new UserCredentials(userAndPass[0], userAndPass[1]));
                        if(verified){
                            username = userAndPass[0];
                           out.println("You are logged on: " + username);
                        }else{
                            out.println("Invalid username/password");
                        }
                        break;
                    case "logout":
                        serv.logout(username);
                        break;
                    case "createdir":
                        serv.createDir(commands[1], username);
                        out.println("Directory " + commands[1] + " created");
                        break;
                    case "deletedir":
                        serv.deleteDir(commands[1], username);
                        out.println("Directory " + commands[1] + " deleted");
                        break;
                    case "list":
                       serv.list(commands[1], username);
                        break;
                    case "write":
                       fileAndString = commands[1].split(" ", 2);
                       serv.updatefile(fileAndString[0], fileAndString[1]);
                        break;
                    case "read":
                        serv.read(commands[1], username);
                        break;
                    case "copy":
                        fileAndString = commands[1].split(" ", 2);
                        //contr.copy(fileAndString[0], fileAndString[1]);
                        break;
                    case "uploadfile":
                        Path dir = Paths.get("Files");
                        Path from = dir.resolve(Paths.get(commands[1]));
                        byte[] data = Files.readAllBytes(from);
                        serv.uploadFile(data, commands[1]);
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
private void servlookup(String host) throws NotBoundException, MalformedURLException, RemoteException{
    serv = (Fserver) Naming.lookup("//" + host + "/" + Fserver.RegName);
}

 private class ConsoleOutput extends UnicastRemoteObject implements Fclient {
        public ConsoleOutput() throws RemoteException {
        }

        @Override
        public void getMsg(String msg) {
            out.println((String) msg);
        }
    }

private class ResultHandle implements Consumer {
        @Override
        public void accept(Object msg) {
            out.println((String)msg);
        }
    }
}