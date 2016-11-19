package com.github.funthomas424242.jpacker4gramps;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

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

    public boolean isValidGPKGArchive() throws MagicParseException,
            MagicMatchNotFoundException, MagicException {

        final MagicMatch match = Magic.getMagicMatch(file, false);
        return "application/x-gzip".equals(match.getMimeType());
    }

    public boolean isValidTARArchive() throws MagicParseException,
            MagicMatchNotFoundException, MagicException {

        final MagicMatch match = Magic.getMagicMatch(file, false);
        return "application/x-tar".equals(match.getMimeType());
    }

    public void unzipGPKGArchive(final File unzipedFile)
            throws FileNotFoundException, IOException {

        FileInputStream fin = new FileInputStream(file);
        BufferedInputStream in = new BufferedInputStream(fin);
        FileOutputStream out = new FileOutputStream(unzipedFile);
        GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
        final byte[] buffer = new byte[1024];
        int n = 0;
        while (-1 != (n = gzIn.read(buffer))) {
            out.write(buffer, 0, n);
        }
        out.close();
        gzIn.close();
    }
}
