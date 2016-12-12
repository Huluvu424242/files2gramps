package com.github.funthomas424242.files2gramps;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrampsExporter {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    protected Path tmpFolderPath;
    protected File grampsFile;
    protected File targetArchive;

    protected File mediaFolder;

    public GrampsExporter(final String tmpFolderPrefix, final File grampsFile,
                          final File targetArchive, final File mediaFolder)
            throws IOException {

        if (tmpFolderPrefix == null || tmpFolderPrefix.length() < 1
                || grampsFile == null || targetArchive == null) {
            throw new IllegalArgumentException();
        }
        this.tmpFolderPath = Files.createTempDirectory(tmpFolderPrefix);
        this.grampsFile = grampsFile;
        this.mediaFolder = mediaFolder;
        this.targetArchive = targetArchive;
    }

    public GrampsExporter(final String tmpFolderPrefix, final File grampsFile,
                          final File targetArchive) throws IOException {
        this(tmpFolderPrefix, grampsFile, targetArchive, null);
    }

    protected void createTmpFolder() {
        tmpFolderPath.toFile().mkdirs();
    }

    /**
     * Creates a archive file without content.
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    protected void createArchivefile()
            throws IOException, FileNotFoundException {
        final File archivFolder = targetArchive.getParentFile();
        if (archivFolder != null) {
            archivFolder.mkdirs();
        }
        if (!targetArchive.createNewFile()) {
            final FileHelper fileHelper = new FileHelper(targetArchive);
            fileHelper.clearFile();
        }
    }

    protected void createZippedGrampsFile() throws IOException {
        final File zippedGrampsFile = new File(tmpFolderPath.toFile(),
                "data.gramps");
        zippedGrampsFile.createNewFile();
        final FileOutputStream fileOutputStream = new FileOutputStream(
                zippedGrampsFile);
        final BufferedOutputStream bufOutStream = new BufferedOutputStream(
                fileOutputStream);
        final GzipCompressorOutputStream gzipOutStream = new GzipCompressorOutputStream(
                bufOutStream);

        final FileInputStream fin = new FileInputStream(this.grampsFile);
        final BufferedInputStream in = new BufferedInputStream(fin);
        IOUtils.copy(in, gzipOutStream);

        gzipOutStream.close();
        in.close();
    }

    protected void addZippedGrampsFile(
            final TarArchiveOutputStream tarOutStream) throws IOException {

        final File zippedGrampsFile = new File(tmpFolderPath.toFile(),
                "data.gramps");
        TarArchiveEntry entry = new TarArchiveEntry(zippedGrampsFile,
                "data.gramps");
        entry.setSize(zippedGrampsFile.length());
        tarOutStream.putArchiveEntry(entry);

        final FileInputStream fin = new FileInputStream(zippedGrampsFile);
        final BufferedInputStream in = new BufferedInputStream(fin);
        IOUtils.copy(in, tarOutStream);

        tarOutStream.closeArchiveEntry();
        in.close();
    }

    protected void addMediaFolderFiles(
            final TarArchiveOutputStream tarOutStream) throws IOException {
        if (mediaFolder == null) {
            return;
        }
        final File mediaParentFile = mediaFolder.getParentFile();
        final String mediaFolderEntryPrefix = mediaFolder.getCanonicalPath().substring
                (mediaParentFile.getCanonicalPath().length() + 1, mediaFolder.getCanonicalPath().length());
        final FileHelper fileHelper = new FileHelper(mediaFolder);

        for (File file : fileHelper.getRecursiveFilelistOfFolder()) {
            logger
                    .debug("Add to archive media file: " + file.getAbsolutePath());
            final String entryName = mediaFolderEntryPrefix+"/"+file.getCanonicalPath().substring(
                    mediaFolder.getCanonicalPath().length() + 1,
                    file.getCanonicalPath().length());
            logger.debug("as tar entry with name: " + entryName);
            final TarArchiveEntry tarEntry = new TarArchiveEntry(file,
                    entryName);
            tarEntry.setSize(file.length());
            tarOutStream.putArchiveEntry(tarEntry);
            final FileInputStream inStream = new FileInputStream(file);
            IOUtils.copy(inStream, tarOutStream);
            tarOutStream.closeArchiveEntry();
            tarOutStream.flush();
            inStream.close();
        }

    }

    public File createExportfile() throws IOException, CompressorException {
        this.createTmpFolder();
        this.createArchivefile();
        this.createZippedGrampsFile();

        final FileOutputStream file_out = new FileOutputStream(targetArchive);
        final BufferedOutputStream buffer_out = new BufferedOutputStream(
                file_out);
        final GzipCompressorOutputStream gzip_out = new GzipCompressorOutputStream(
                buffer_out);
        final TarArchiveOutputStream tarOut = new TarArchiveOutputStream(
                gzip_out);

        addMediaFolderFiles(tarOut);
        this.addZippedGrampsFile(tarOut);

        tarOut.close();
        return this.targetArchive;
    }
}
