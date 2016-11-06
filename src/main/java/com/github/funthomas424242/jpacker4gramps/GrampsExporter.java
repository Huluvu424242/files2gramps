package com.github.funthomas424242.jpacker4gramps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GrampsExporter {

    protected File grampsFile;
    protected File targetArchive;

    protected File mediaFolder;

    public GrampsExporter(final File grampsFile, final File targetArchive,
            final File mediaFolder) {

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
