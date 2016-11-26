# files2gramps
Java lib for files to pack as a export into gramps database format.

[![Build Status](https://travis-ci.org/FunThomas424242/files2gramps.svg?branch=master)](https://travis-ci.org/FunThomas424242/files2gramps)
[![codecov](https://codecov.io/gh/FunThomas424242/files2gramps/branch/master/graph/badge.svg)](https://codecov.io/gh/FunThomas424242/files2gramps)
 [ ![Download](https://api.bintray.com/packages/funthomas424242/funthomas424242-maven-plugins/files2gramps/images/download.svg) ](https://bintray.com/funthomas424242/funthomas424242-maven-plugins/files2gramps/_latestVersion)


# Usage
You need:
 
 * the prefix of tmpFolder as String
 * the gramps database file (in plain text xml format) as File
 * the target archive file as File 
 * the media folder as File
 
Then you can make a instance of GrampsExporter and call the createExportfile() method.
See the following code snippet how to use.

```
@Test
public void createValidTargetArchivefile_WithValidMediaFolder()
        throws FileNotFoundException, IOException, MagicParseException,
        MagicMatchNotFoundException, MagicException, CompressorException {
    // prepare 
    final String targetArchivFileName = "target/test/created"
            + System.currentTimeMillis() + "/new";
    final String tmpFolderPrefix = "tmpFolderBeispiel2";

    final GrampsExporter exporter = new GrampsExporter(tmpFolderPrefix,
            grampsDatabasFile, new File(targetArchivFileName), mediaFolder);
    // execution
    final File exportFile = exporter.createExportfile();
    
    // asserts
    assertTrue("Archiv wurde nicht angelegt", exportFile.exists());
    final FileHelper fileHelper = new FileHelper(exportFile);
    assertTrue("Not a valid gramps archive",
            fileHelper.isValidGPKGArchive());
} 
```
