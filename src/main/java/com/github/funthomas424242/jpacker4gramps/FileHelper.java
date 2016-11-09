package com.github.funthomas424242.jpacker4gramps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {

    protected Logger logger = LoggerFactory.getLogger(getClass().getName());

    protected final File file;

    public FileHelper(final File file) {
        if (file == null) {
            throw new IllegalArgumentException();
        }
        this.file = file;
        logger.info("File path absolut:" + file.getAbsolutePath());
    }

    public String getFileContent() throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    public List<File> getDirectorylistOfFolder() {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException();
        }
        final File[] files = file.listFiles(new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                return new File(dir, name).isDirectory();
            }
        });
        return Arrays.asList(files);
    }

    public List<File> getFilelistOfFolder() {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException();
        }
        final File[] files = file.listFiles(new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                return new File(dir, name).isFile();
            }
        });
        return Arrays.asList(files);
    }

    public List<File> getRecursiveFilelistOfFolder() {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException();
        }
        final List<File> fileList = new ArrayList<File>();
        final Stack<File> folderStack = new Stack<File>();
        folderStack.push(file);

        while (!folderStack.isEmpty()) {
            final File curFolder = folderStack.pop();

            final File[] folders = curFolder.listFiles(new FilenameFilter() {
                public boolean accept(final File dir, final String name) {
                    return new File(dir, name).isDirectory();
                }
            });
            folderStack.addAll(Arrays.asList(folders));

            final File[] files = curFolder.listFiles(new FilenameFilter() {
                public boolean accept(final File dir, final String name) {
                    return new File(dir, name).isFile();
                }
            });
            fileList.addAll(Arrays.asList(files));
        }
        return fileList;
    }

    public void clearFile() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(this.file);
        writer.print("");
        writer.flush();
        writer.close();
    }
}
