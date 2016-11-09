package com.github.funthomas424242.jpacker4gramps;

import static org.junit.Assert.assertTrue;

import java.io.File;

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
        final File grampsDatabasFile = new File(
                config.getString("beispiel1.gramps.file"));
        final File mediaFolder = new File(
                config.getString("beispiel1.media.folder"));
        final File targetArchive = new File(
                config.getString("beispiel1.target.archiv.name") + ".tgz");
        exporter = new GrampsExporter(grampsDatabasFile, targetArchive,
                mediaFolder);
    }

    @Test
    public void createTargetArchivefile() {
        // prepare -> @Before

        // execution
        exporter.createArchivefile();

        // asserts
        final File targetArchivFile = new File(
                config.getString(PROP_TARGET_ARCHIV_FILENAME) + ".tgz");
        assertTrue("Archiv wurde nicht angelegt", targetArchivFile.exists());
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
