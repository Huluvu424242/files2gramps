package com.github.funthomas424242.jpacker4gramps;

import java.io.File;

public class GrampsExporter {

    protected File grampsFile;
    protected File mediaFolder;
    protected File targetArchive;

    public GrampsExporter(final File grampsFile, final File mediaFolder,
            final File targetArchive) {

        this.grampsFile = grampsFile;
        this.mediaFolder = mediaFolder;
        this.targetArchive = targetArchive;
    }

}
