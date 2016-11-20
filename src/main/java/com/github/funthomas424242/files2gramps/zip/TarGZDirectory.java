package com.github.funthomas424242.files2gramps.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class TarGZDirectory {

    public static void createTarGZ(final String quellDirectoryPath,
            final String targetFilePath)
            throws FileNotFoundException, IOException {

        FileOutputStream fOut = null;
        BufferedOutputStream bOut = null;
        GzipCompressorOutputStream gzOut = null;
        TarArchiveOutputStream tOut = null;

        try {
            System.out.println("LOG:" + new File(".").getAbsolutePath());
            final String dirPath = quellDirectoryPath;
            //final String dirPath = "parent/childDirToCompress/";
            final String tarGzPath = targetFilePath;
            final File tarGzFile = new File(tarGzPath);
            final File targetFolder = tarGzFile.getParentFile();
            targetFolder.mkdirs();
            if (!tarGzFile.exists()) {
                tarGzFile.createNewFile();
            }
            fOut = new FileOutputStream(tarGzFile);
            bOut = new BufferedOutputStream(fOut);
            gzOut = new GzipCompressorOutputStream(bOut);
            tOut = new TarArchiveOutputStream(gzOut);
            addFileToTarGz(tOut, dirPath, "");
        } finally {
            tOut.finish();
            tOut.close();
            gzOut.close();
            bOut.close();
            fOut.close();
        }
    }

    private static void addFileToTarGz(final TarArchiveOutputStream tOut,
            final String path, final String base) throws IOException {
        final File f = new File(path);
        System.out.println("LOG:" + f.exists());
        final String entryName = base + f.getName();
        final TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
        tOut.putArchiveEntry(tarEntry);

        if (f.isFile()) {
            IOUtils.copy(new FileInputStream(f), tOut);
            tOut.closeArchiveEntry();
        } else {
            tOut.closeArchiveEntry();
            final File[] children = f.listFiles();
            if (children != null) {
                for (final File child : children) {
                    System.out.println(child.getName());
                    addFileToTarGz(tOut, child.getAbsolutePath(),
                            entryName + "/");
                }
            }
        }
    }
}