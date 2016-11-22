package com.github.funthomas424242.files2gramps;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
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

    /**
     * 
     * @param fin
     * @return
     * @throws IOException
     */
    protected GzipCompressorInputStream zipContentOfStream(
            final FileInputStream fin) throws IOException {

        //TODO aktuell Deadlock wegen Single Thread. 
        // Das zippen soll ein Hilfsthread erledigen
        final BufferedInputStream in = new BufferedInputStream(fin);
        final PipedInputStream pIn = new PipedInputStream();
        final PipedOutputStream pOut = new PipedOutputStream(pIn);
        final GzipCompressorInputStream gzIn = new GzipCompressorInputStream(
                pIn);
        final GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(
                pOut);
        IOUtils.copy(in, gzOut);
        return gzIn;
    }

    protected void addZippedGrampsFile(final TarArchiveOutputStream tarOut)
            throws IOException {

        final FileInputStream fin = new FileInputStream(this.grampsFile);
        final GzipCompressorInputStream gzip = zipContentOfStream(fin);

        final TarArchiveEntry entry = new TarArchiveEntry(this.grampsFile,
                this.grampsFile.getName());
        //entry.setSize(this.grampsFile.length());
        tarOut.putArchiveEntry(entry);
        IOUtils.copy(gzip, tarOut);
        tarOut.closeArchiveEntry();
    }

    protected void addMediaFolderFiles() {

    }

    public File createExportfile() throws IOException, CompressorException {
        this.createTmpFolder();
        this.createArchivefile();

        final FileOutputStream file_out = new FileOutputStream(targetArchive);
        final BufferedOutputStream bufferOut = new BufferedOutputStream(
                file_out);
        final GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(
                bufferOut);
        final TarArchiveOutputStream tarOut = new TarArchiveOutputStream(
                gzipOut);

        this.addZippedGrampsFile(tarOut);

        tarOut.close();
        return this.targetArchive;
    }
}
