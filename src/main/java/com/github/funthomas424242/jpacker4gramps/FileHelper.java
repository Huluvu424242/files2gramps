package com.github.funthomas424242.jpacker4gramps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

}
