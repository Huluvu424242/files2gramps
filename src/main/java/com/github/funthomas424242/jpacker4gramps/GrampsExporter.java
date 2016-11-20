package com.github.funthomas424242.jpacker4gramps;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
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
        final ZipArchiveOutputStream zipOutStream = new ZipArchiveOutputStream(
                zippedGrampsFile);
        final ZipArchiveEntry entry = new ZipArchiveEntry(zippedGrampsFile,
                "data.gramps");
        entry.setSize(zippedGrampsFile.length());
        final FileInputStream fin = new FileInputStream(this.grampsFile);
        final BufferedInputStream in = new BufferedInputStream(fin);

        zipOutStream.putArchiveEntry(entry);
        final byte[] buffer = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buffer))) {
            zipOutStream.write(buffer, 0, n);
        }
        zipOutStream.closeArchiveEntry();
        zipOutStream.close();
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

        final byte[] buffer = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buffer))) {
            tarOutStream.write(buffer, 0, n);
        }
        tarOutStream.closeArchiveEntry();
        in.close();
    }

    protected void addMediaFolderFiles() {

    }

    protected void writeArchiveFileContentAndClose() {

    }

    public File createExportfile() throws IOException, CompressorException {
        this.createTmpFolder();
        this.createArchivefile();
        this.createZippedGrampsFile();

        final File tarFile = new File(this.tmpFolder, targetArchive.getName());
        final FileOutputStream fOut = new FileOutputStream(tarFile);
        final TarArchiveOutputStream tarOut = new TarArchiveOutputStream(fOut);
        this.addZippedGrampsFile(tarOut);
        tarOut.close();

        // tar file gzippen
        final FileInputStream fin = new FileInputStream(tarFile);
        final BufferedInputStream in = new BufferedInputStream(fin);
        final FileOutputStream fileOutputStream = new FileOutputStream(
                targetArchive);
        final BufferedOutputStream bufOutStream = new BufferedOutputStream(
                fileOutputStream);
        final CompressorOutputStream gzippedOut = new CompressorStreamFactory()
            .createCompressorOutputStream(CompressorStreamFactory.GZIP,
                    bufOutStream);
        final byte[] buffer = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buffer))) {
            gzippedOut.write(buffer, 0, n);
        }
        gzippedOut.close();
        in.close();

        return this.targetArchive;
    }
}
