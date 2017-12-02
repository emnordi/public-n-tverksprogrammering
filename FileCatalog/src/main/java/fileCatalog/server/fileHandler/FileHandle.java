/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.server.fileHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Emil
 */
public class FileHandle {

    private Path currentDirectory = Paths.get("Files");
    private Path uploadDir = Paths.get("Files/uploads");

    public void makeDirectory(String path) throws IOException {
        Path dirPath = currentDirectory.resolve(Paths.get(path));
        if (Files.exists(dirPath)) {
            return;
        }
        Files.createDirectory(dirPath);
    }

    public void deleteDirectory(String path) throws IOException {
        Path dirPath = currentDirectory.resolve(Paths.get(path));
        if (Files.exists(dirPath)) {
            Files.delete(dirPath);
        }
        return;
    }

    public String listDir(String path) throws IOException {
        Path dirPath = currentDirectory.resolve(Paths.get(path));
        if (Files.isDirectory(dirPath)) {
            StringBuilder content = new StringBuilder();
            Files.list(dirPath).forEach(
                    fileInDir -> stringBuild(content, fileInDir.toString()));
            return content.toString().trim().replaceAll("\\./", "");
        }
        return dirPath.toString();
    }

    public void updateFile(String path, String content) throws IOException, ClassNotFoundException {
        String file = currentDirectory.resolve(Paths.get(path)).toString();
        if (type(file, ".txt")) {
            //Opens filewriter with path to file true sees that file is updated not overwritten 
            //Bufferedwriter to send several characters and pruntwriter where data is entered
            try (PrintWriter toFile = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
                toFile.println(content);
            }
        } else if (type(file, ".dat")) {
            //writeHex(file, content);
        } else if (type(file, ".ser")) {
            //writeObj(file, content);
        }
    }

    private boolean type(String file, String extension) {
        return file.endsWith(extension);
    }

    public String read(String path) throws IOException, ClassNotFoundException {
        String file = currentDirectory.resolve(Paths.get(path)).toString();
        if (type(file, ".txt")) {
            try (BufferedReader fromFile = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                fromFile.lines().forEachOrdered(line -> stringBuild(content, line));
                return content.toString().trim();

            }
        } else if (type(file, ".dat")) {
            //  return readHex(file);
        } else if (type(file, ".ser")) {
            // return readObj(file);
        }
        return null;
    }

    private void stringBuild(StringBuilder all, String one) {
        all.append(one);
        all.append(" ");
    }

    //Copy a file using java.nio and transfers
    public void copyFile(String pathFrom, String pathTo) throws FileNotFoundException {
        String from = currentDirectory.resolve(Paths.get(pathFrom)).toString();
        String to = currentDirectory.resolve(Paths.get(pathTo)).toString();
        try {
            try (FileInputStream fis = new FileInputStream(from);
                    FileOutputStream fos = new FileOutputStream(to)) {
                FileChannel inChannel = fis.getChannel();
                FileChannel outChannel = fos.getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Uploads a file to server
    public void uploadFile(byte[] bfile, String pathTo) throws FileNotFoundException {
        Path to = uploadDir.resolve(Paths.get(pathTo));
        try {
            Files.write(to, bfile);
        }catch (IOException e) {
            e.printStackTrace();
    }

}
    //Downloads a file to server
    public void downloadFile(String filename) throws FileNotFoundException {
        try {
        Path from = uploadDir.resolve(Paths.get(filename));
        Path to = currentDirectory.resolve(Paths.get("."));
        byte[] data = Files.readAllBytes(from);
        Files.write(to, data);
        }catch (IOException e) {
            e.printStackTrace();
    }

}
    //Deletes a file from server
    public void deleteFile(String filePath) throws IOException {
        Path file = uploadDir.resolve(Paths.get(filePath));
        if (Files.exists(file)) {
            Files.delete(file);
        }
        return;

}
}
