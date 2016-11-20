package com.github.funthomas424242.files2gramps;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.funthomas424242.files2gramps.FileHelper;
import com.github.funthomas424242.files2gramps.GrampsExporter;

import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

public class GrampsExporterTest {

    private static Logger logger = LoggerFactory
        .getLogger(GrampsExporterTest.class.getName());

    private static final String TEST_PROPERTIES_FILENAME = "test.properties";
    private static final String PROP_TARGET_INVALIDARCHIV_FILENAME = "beispiel1.target.invalidarchiv.name";
    private static final String PROP_TARGET_ARCHIV_FILENAME = "beispiel1.target.archiv.name";
    private static final String PROP_GRAMPS_FILENAME = "beispiel1.gramps.file";

    protected static Configuration config;

    private File tmpFolder;
    private File grampsDatabasFile;
    private File mediaFolder;

    private File targetArchive;
    private File invalidTargetArchive;

    @BeforeClass
    public static void init() {
        Configurations configs = new Configurations();
        try {
            config = configs.properties(new File(TEST_PROPERTIES_FILENAME));
        } catch (ConfigurationException e) {
            logger.debug(TEST_PROPERTIES_FILENAME, e);
        }
    }

    @Before
    public void setUp() {
        tmpFolder = new File("target/test/packer/tmpFolder");
        tmpFolder.mkdirs();
        grampsDatabasFile = new File(config.getString(PROP_GRAMPS_FILENAME));
        mediaFolder = new File(config.getString("beispiel1.media.folder"));
        invalidTargetArchive = new File(
                config.getString(PROP_TARGET_INVALIDARCHIV_FILENAME) + ".tgz");
        targetArchive = new File(
                config.getString(PROP_TARGET_ARCHIV_FILENAME) + ".tgz");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTargetArchivfile_InvalidFiles() {
        new GrampsExporter(null, null, null);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTargetArchivfile_InvalidGrampsfile() {
        new GrampsExporter(null, null,
                new File("target/test/create/create1.tgz"));
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTargetArchivfile_InvalidArchivefile() {
        new GrampsExporter(null, new File("target/test/create/create2.tgz"),
                null);
        fail();
    }

    @Test
    public void createInvalidTargetArchivefile_ValidGrampsAndExistingArchivfile()
            throws IOException {
        // prepare    
        this.invalidTargetArchive.getParentFile().mkdirs();
        this.invalidTargetArchive.createNewFile();
        final GrampsExporter exporter = new GrampsExporter(tmpFolder,
                grampsDatabasFile, this.invalidTargetArchive);
        // execution
        exporter.createArchivefile();

        // asserts
        assertTrue("Archiv wurde nicht angelegt",
                invalidTargetArchive.exists());
    }

    @Test
    public void createInvalidTargetArchivefile_ValidGrampsAndNewArchivfile()
            throws IOException {
        // prepare 
        final String fileName = "target/test/created/new"
                + System.currentTimeMillis();
        final GrampsExporter exporter = new GrampsExporter(tmpFolder,
                grampsDatabasFile, new File(fileName));
        // execution
        exporter.createArchivefile();

        // asserts
        final File targetArchivFile = new File(fileName);
        assertTrue("Archiv wurde nicht angelegt", targetArchivFile.exists());
    }

    @Test
    public void createTargetArchivefile_NewArchivFolder()
            throws FileNotFoundException, IOException {
        // prepare 
        final String fileName = "target/test/created"
                + System.currentTimeMillis() + "/new";
        final GrampsExporter exporter = new GrampsExporter(tmpFolder,
                grampsDatabasFile, new File(fileName));
        // execution
        exporter.createArchivefile();

        // asserts
        final File targetArchivFile = new File(fileName);
        assertTrue("Archiv wurde nicht angelegt", targetArchivFile.exists());
    }

    @Test(expected = FileNotFoundException.class)
    public void createInvalidTargetArchivefile_NoArchivFolder()
            throws IOException {
        // prepare 
        final String fileName = "/";
        final GrampsExporter exporter = new GrampsExporter(tmpFolder,
                grampsDatabasFile, new File(fileName));
        // execution
        exporter.createArchivefile();
        // asserts
    }

    @Test
    public void createValidTargetArchivefile_NewArchivFolderAndGrampsFile()
            throws FileNotFoundException, IOException, MagicParseException,
            MagicMatchNotFoundException, MagicException, CompressorException {
        // prepare 
        final String targetArchivFileName = "target/test/created"
                + System.currentTimeMillis() + "/new";
        final File tmpFolder = new File("target/test/beispiel2/tmpFolder");

        final GrampsExporter exporter = new GrampsExporter(tmpFolder,
                grampsDatabasFile, new File(targetArchivFileName));
        // execution
        final File exportFile = exporter.createExportfile();

        // asserts
        assertTrue("Archiv wurde nicht angelegt", exportFile.exists());
        final FileHelper fileHelper = new FileHelper(exportFile);
        assertTrue("Not a valid gramps archive",
                fileHelper.isValidGPKGArchive());
    }

}
