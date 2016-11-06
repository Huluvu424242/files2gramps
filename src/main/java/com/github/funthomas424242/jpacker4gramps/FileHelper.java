package com.github.funthomas424242.jpacker4gramps;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {

    protected Logger logger = LoggerFactory.getLogger(getClass().getName());

    protected final File file;

    public FileHelper(final File file) {
        this.file = file;
        logger.debug("File path absolut:" + file.getAbsolutePath());
    }

    public String getFileContent() throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
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

}
