/*
package fileCatalog.client.controller;

import fileCatalog.all.Fserver;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import fileCatalog.server.fileHandler.FileHandle;
import java.util.function.Consumer;
import fileCatalog.server.integration.FileDAO;

public class Controller {
    private long userId;
    FileHandle fileHandle = new FileHandle();
    Fserver server;
    
    public void createDir(String path) {
        CompletableFuture.runAsync(() -> {
            try {
                fileHandle.makeDirectory(path);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }

    public void deleteDir(String path) {
        CompletableFuture.runAsync(() -> {
            try {
                fileHandle.deleteDirectory(path);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }

    public void listDir(String path, Consumer display) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return fileHandle.listDir(path);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }).thenAccept(display);
    }

    public void write(String path, String content) {
        CompletableFuture.runAsync(() -> {
            try {
                fileHandle.updateFile(path, content);
            } catch (IOException | ClassNotFoundException ioe) {
                System.err.println("Did not work");
            }
        });
    }

    public void copy(String pathFrom, String pathTo) {
        CompletableFuture.runAsync(() -> {
            try {
                fileHandle.copyFile(pathFrom, pathTo);
            } catch (Exception e) {
                System.err.println("Did not work");
            }
        });
    }

    public void read(String path, Consumer showOutput) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return fileHandle.read(path);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            } catch (ClassNotFoundException ioe) {
                throw new RuntimeException(ioe);
            }
        }).thenAccept(showOutput);
    }

}
*/