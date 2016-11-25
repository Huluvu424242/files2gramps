package com.github.funthomas424242.files2gramps;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;
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
            MagicMatchNotFoundException, MagicException, IOException {

        final Set<String> mimeTypes = getDetectedMimeTypesOf();
        return mimeTypes.contains("application/x-gramps-package")
                || mimeTypes.contains("application/x-gzip");
    }

    public boolean isValidTARArchive() throws MagicParseException,
            MagicMatchNotFoundException, MagicException {

        final String mimeType = getAndlogMagicNumberMimeType();
        return "application/x-tar".equals(mimeType);
    }

    public boolean isValidZipArchive() throws MagicParseException,
            MagicMatchNotFoundException, MagicException, IOException {

        final Set<String> mimeTypes = getDetectedMimeTypesOf();
        return mimeTypes.contains("application/x-gzip");
    }

    public boolean isValidGrampsXmlFile() throws IOException,
            MagicParseException, MagicMatchNotFoundException, MagicException {

        final Set<String> mimeTypes = getDetectedMimeTypesOf();
        return mimeTypes.contains("application/x-gramps-xml")
                || mimeTypes.contains("text/plain");
    }

    protected Set<String> getDetectedMimeTypesOf() throws MagicParseException,
            MagicMatchNotFoundException, MagicException, IOException {

        final Set<String> mimeTypes = new HashSet<>();
        final String magicMimeType = getAndlogMagicNumberMimeType();
        if (magicMimeType != null) {
            mimeTypes.add(magicMimeType);
        }
        final String systemMimeType = getAndlogSystemMimeType();
        if (systemMimeType != null) {
            mimeTypes.add(systemMimeType);
        }
        return mimeTypes;
    }

    protected String getAndlogMagicNumberMimeType() {

        if (file.length() < 1) {
            logger.warn("file without content: " + file.getAbsolutePath());
            return null;
        }

        String mimeType = null;
        try {
            final MagicMatch match = Magic.getMagicMatch(file, true);
            mimeType = match.getMimeType();
        } catch (MagicParseException | MagicMatchNotFoundException
                | MagicException e) {
            logger.error("invalid file structure", e);
        }
        logger.debug("Mime-Type: " + mimeType);
        return mimeType;
    }

    protected String getAndlogSystemMimeType() throws IOException {
        final String mimeType = Files.probeContentType(file.toPath());
        logger.debug("Mime-Type: " + mimeType);
        return mimeType;
    }

    public void unzipArchiveTo(final File unzipedFile)
            throws FileNotFoundException, IOException {

        final FileInputStream fin = new FileInputStream(file);
        final BufferedInputStream in = new BufferedInputStream(fin);
        final FileOutputStream out = new FileOutputStream(unzipedFile);
        final GzipCompressorInputStream gzIn = new GzipCompressorInputStream(
                in);
        IOUtils.copy(gzIn, out);
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
                IOUtils.copy(tarIn, fout);
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
