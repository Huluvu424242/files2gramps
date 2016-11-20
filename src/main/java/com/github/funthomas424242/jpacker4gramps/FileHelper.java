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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
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
        final String mimeType = getAndlogMimeType(match);
        return "application/x-gzip".equals(mimeType);
    }

    public boolean isValidTARArchive() throws MagicParseException,
            MagicMatchNotFoundException, MagicException {

        final MagicMatch match = Magic.getMagicMatch(file, false);
        final String mimeType = getAndlogMimeType(match);
        return "application/x-tar".equals(mimeType);
    }

    public boolean isValidZipArchive() throws MagicParseException,
            MagicMatchNotFoundException, MagicException {
        return isValidGPKGArchive();
    }

    public boolean isValidGrampsXmlFile() throws IOException {

        final String mimeType = Files.probeContentType(file.toPath());
        logger.debug("Mime-Type: " + mimeType);
        return "application/x-gramps-xml".equals(mimeType);
    }

    protected String getAndlogMimeType(final MagicMatch match) {
        final String mimeType = match.getMimeType();
        logger.debug("Mime-Type: " + mimeType);
        return mimeType;
    }

    public void unzipArchiveTo(final File unzipedFile)
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

    public void untarFileToDirectory(final File targetFolder)
            throws IllegalArgumentException, FileNotFoundException, IOException,
            MagicParseException, MagicMatchNotFoundException, MagicException {

        if (!targetFolder.isDirectory()) {
            throw new IllegalArgumentException("not a valid target folder: "
                    + targetFolder.getAbsolutePath());
        }
        if (!this.isValidTARArchive()) {
            throw new IllegalArgumentException(
                    "not a valid tar file: " + file.getAbsolutePath());
        }

        final FileInputStream fin = new FileInputStream(file);
        final BufferedInputStream in = new BufferedInputStream(fin);
        final TarArchiveInputStream tarIn = new TarArchiveInputStream(in);
        TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
        // tarIn is a TarArchiveInputStream
        while (tarEntry != null) {// create a file with the same name as the tarEntry
            final File destFile = new File(targetFolder, tarEntry.getName());
            if (tarEntry.isDirectory()) {
                logger.debug("mkdir: " + destFile.getCanonicalPath());
                destFile.mkdirs();
            } else {
                logger.debug("untarring file: " + destFile.getCanonicalPath());
                destFile.createNewFile();
                final FileOutputStream fout = new FileOutputStream(destFile);
                final byte[] buffer = new byte[1024];
                int n = 0;
                while (-1 != (n = tarIn.read(buffer))) {
                    fout.write(buffer, 0, n);
                }
                fout.close();
            }
            tarEntry = tarIn.getNextTarEntry();
        }
        tarIn.close();
    }

    public void extractGrampsXMLTo(final File tmpTarFile,
            final File tmpTARFolder, final File targetGrampsFile)
            throws FileNotFoundException, IOException, IllegalArgumentException,
            MagicParseException, MagicMatchNotFoundException, MagicException {

        unzipArchiveTo(tmpTarFile);
        final FileHelper tarHelper = new FileHelper(tmpTarFile);
        tarHelper.untarFileToDirectory(tmpTARFolder);
        final File tmpGrampsZipFile = new File(tmpTARFolder, "data.gramps");
        final FileHelper zipHelper = new FileHelper(tmpGrampsZipFile);
        zipHelper.unzipArchiveTo(targetGrampsFile);
    }

}
