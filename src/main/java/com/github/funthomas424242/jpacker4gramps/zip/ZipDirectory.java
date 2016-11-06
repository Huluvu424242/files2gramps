/*****************************************************************/
/* Copyright 2013 Code Strategies                                */
/* This code may be freely used and distributed in any project.  */
/* However, please do not remove this credit if you publish this */
/* code in paper or electronic form, such as on a web site.      */
/*****************************************************************/
// downloaded from:
// http://www.avajava.com/tutorials/files/how-do-i-zip-a-directory-and-all-its-contents/ZipDirectory.java */                                                   */
package com.github.funthomas424242.jpacker4gramps.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipDirectory {

    public static void main(final String[] args) throws IOException {
        final File directoryToZip = new File(
                "C:\\projects\\workspace\\testing\\stuff");

        final List<File> fileList = new ArrayList<File>();
        System.out.println("---Getting references to all files in: "
                + directoryToZip.getCanonicalPath());
        getAllFiles(directoryToZip, fileList);
        System.out.println("---Creating zip file");
        writeZipFile(directoryToZip, fileList);
        System.out.println("---Done");
    }

    public static void getAllFiles(final File dir, final List<File> fileList) {
        try {
            final File[] files = dir.listFiles();
            for (final File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    System.out.println("directory:" + file.getCanonicalPath());
                    getAllFiles(file, fileList);
                } else {
                    System.out.println("     file:" + file.getCanonicalPath());
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeZipFile(final File directoryToZip,
            final List<File> fileList) {

        try {
            final FileOutputStream fos = new FileOutputStream(
                    directoryToZip.getName() + ".zip");
            final ZipOutputStream zos = new ZipOutputStream(fos);

            for (final File file : fileList) {
                if (!file.isDirectory()) { // we only zip files, not directories
                    addToZip(directoryToZip, file, zos);
                }
            }

            zos.close();
            fos.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void addToZip(final File directoryToZip, final File file,
            final ZipOutputStream zos)
            throws FileNotFoundException, IOException {

        final FileInputStream fis = new FileInputStream(file);

        // we want the zipEntry's path to be a relative path that is relative
        // to the directory being zipped, so chop off the rest of the path
        final String zipFilePath = file.getCanonicalPath().substring(
                directoryToZip.getCanonicalPath().length() + 1,
                file.getCanonicalPath().length());
        System.out.println("Writing '" + zipFilePath + "' to zip file");
        final ZipEntry zipEntry = new ZipEntry(zipFilePath);
        zos.putNextEntry(zipEntry);

        final byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }

}