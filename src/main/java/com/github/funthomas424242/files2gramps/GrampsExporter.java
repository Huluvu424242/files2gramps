package com.github.funthomas424242.files2gramps;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrampsExporter {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    protected File tmpFolder;
    protected File grampsFile;
    protected File targetArchive;

    protected File mediaFolder;

    public GrampsExporter(final File tmpFolder, final File grampsFile,
            final File targetArchive, final File mediaFolder) {

        if (tmpFolder == null || grampsFile == null || targetArchive == null) {
            throw new IllegalArgumentException();
        }
        this.tmpFolder = tmpFolder;
        this.grampsFile = grampsFile;
        this.mediaFolder = mediaFolder;
        this.targetArchive = targetArchive;
    }

    public GrampsExporter(final File tmpFolder, final File grampsFile,
            final File targetArchive) {
        this(tmpFolder, grampsFile, targetArchive, null);
    }

    protected void createTmpFolder() {
        tmpFolder.mkdirs();
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
        final File zippedGrampsFile = new File(tmpFolder, "data.gramps");
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

        final File zippedGrampsFile = new File(tmpFolder, "data.gramps");
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

    protected void addMediaFolderFiles() {

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

        this.addZippedGrampsFile(tarOut);

        tarOut.close();
        return this.targetArchive;
    }
}
