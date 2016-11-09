package com.github.funthomas424242.jpacker4gramps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrampsExporter {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    protected File grampsFile;
    protected File targetArchive;

    protected File mediaFolder;

    public GrampsExporter(final File grampsFile, final File targetArchive,
            final File mediaFolder) {

        if (grampsFile == null || targetArchive == null) {
            throw new IllegalArgumentException();
        }
        this.grampsFile = grampsFile;
        this.mediaFolder = mediaFolder;
        this.targetArchive = targetArchive;
    }

    public GrampsExporter(final File grampsFile, final File targetArchive) {

        this.grampsFile = grampsFile;
        this.mediaFolder = null;
        this.targetArchive = targetArchive;
    }

    protected void createArchivefile() {
        try {
            final File archivFolder = targetArchive.getParentFile();
            if (archivFolder != null) {
                archivFolder.mkdirs();
            }
            if (!targetArchive.createNewFile()) {
                final FileHelper fileHelper = new FileHelper(targetArchive);
                fileHelper.clearFile();
            }
        } catch (IOException e) {
            logger.debug(targetArchive.getAbsolutePath(), e);
        }

    }

    protected void addGampsFile() {

    }

    protected void addMediaFolderFiles() {

    }

    protected void writeArchiveFileAndClose() {

    }

    public FileInputStream createExportfile() throws FileNotFoundException {

        return new FileInputStream(this.targetArchive);
    }
}
