package com.github.funthomas424242.files2gramps;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.funthomas424242.files2gramps.gzip.GZipper;

public class GrampsExporter {

    final protected Logger logger = LoggerFactory
        .getLogger(getClass().getName());

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

    protected void addZippedGrampsFile(final TarArchiveOutputStream tarOut)
            throws IOException, CompressorException {

        final FileInputStream fin = new FileInputStream(this.grampsFile);

        final GZipper zipper = new GZipper(fin);
        final GzipCompressorInputStream gzip = zipper
            .getGzipCompressorInputStream();

        final TarArchiveEntry entry = new TarArchiveEntry(this.grampsFile,
                this.grampsFile.getName());
        tarOut.putArchiveEntry(entry);
        final Thread zipperThread = new Thread(zipper);
        logger.debug("VOR START");
        zipperThread.start();
        logger.debug("NACH START");

        logger.debug("VOR LESEN GZIP");
        IOUtils.copy(gzip, tarOut);
        logger.debug("NACH LESEN GZIP");

        tarOut.closeArchiveEntry();
        tarOut.flush();
        gzip.close();
    }

    protected void addMediaFolderFiles() {

    }

    public File createExportfile() throws IOException, CompressorException {
        this.createTmpFolder();
        this.createArchivefile();

        logger.debug("ARCHIVE: " + targetArchive.getAbsolutePath());
        final FileOutputStream fileOut = new FileOutputStream(targetArchive);
        final BufferedOutputStream bufferOut = new BufferedOutputStream(
                fileOut);
        final GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(
                bufferOut);
        final TarArchiveOutputStream tarOut = new TarArchiveOutputStream(
                gzipOut);

        this.addZippedGrampsFile(tarOut);

        tarOut.close();
        return this.targetArchive;
    }
}
