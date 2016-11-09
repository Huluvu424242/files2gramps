package com.github.funthomas424242.jpacker4gramps;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrampsExporterTest {

    private static Logger logger = LoggerFactory
        .getLogger(GrampsExporterTest.class.getName());

    private static final String TEST_PROPERTIES_FILENAME = "test.properties";
    private static final String PROP_TARGET_ARCHIV_FILENAME = "beispiel1.target.archiv.name";

    protected GrampsExporter exporter;

    protected static Configuration config;

    private File grampsDatabasFile;

    private File mediaFolder;

    private File targetArchive;

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
        grampsDatabasFile = new File(config.getString("beispiel1.gramps.file"));
        mediaFolder = new File(config.getString("beispiel1.media.folder"));
        targetArchive = new File(
                config.getString("beispiel1.target.archiv.name") + ".tgz");
        exporter = new GrampsExporter(grampsDatabasFile, targetArchive,
                mediaFolder);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTargetArchivfile_InvalidFiles() {
        exporter = new GrampsExporter(null, null);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTargetArchivfile_InvalidGrampsfile() {
        exporter = new GrampsExporter(null,
                new File("target/test/create/create1.tgz"));
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTargetArchivfile_InvalidArchivefile() {
        exporter = new GrampsExporter(
                new File("target/test/create/create2.tgz"), null);
        fail();
    }

    @Test
    public void createTargetArchivefile_ValidGrampsAndArchivfile()
            throws IOException {
        // prepare 
        exporter = new GrampsExporter(grampsDatabasFile, targetArchive);
        // execution
        exporter.createArchivefile();

        // asserts
        final File targetArchivFile = new File(
                config.getString(PROP_TARGET_ARCHIV_FILENAME) + ".tgz");
        assertTrue("Archiv wurde nicht angelegt", targetArchivFile.exists());
    }

    @Test
    public void createTargetArchivefile_NewArchivFolder()
            throws FileNotFoundException, IOException {
        // prepare 
        final String fileName = "target/test/created"
                + System.currentTimeMillis() + "/new";
        exporter = new GrampsExporter(grampsDatabasFile, new File(fileName));
        // execution
        exporter.createArchivefile();

        // asserts
        final File targetArchivFile = new File(fileName);
        assertTrue("Archiv wurde nicht angelegt", targetArchivFile.exists());
    }

    @Test(expected = FileNotFoundException.class)
    public void createTargetArchivefile_NoArchivFolder() throws IOException {
        // prepare 
        final String fileName = "/";
        exporter = new GrampsExporter(grampsDatabasFile, new File(fileName));
        // execution
        exporter.createArchivefile();
        // asserts
    }

    //
    //    @Test
    //    public void readContentOfDatabaseFile() {
    //        final StringBuffer content = exporter.getFileContent();
    //        assertEquals(1, content.length());
    //    }
    //
    //    @Test
    //    public void readFilesOfMediaFolder()
    //            throws FileNotFoundException, IOException {
    //
    //    }

}
