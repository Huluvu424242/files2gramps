package com.github.funthomas424242.jpacker4gramps;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Before;
import org.junit.BeforeClass;

public class GrampsExporterTest {

    private static final String TEST_PROPERTIES_FILENAME = "test.properties";

    protected GrampsExporter exporter;

    protected static Configuration config;

    @BeforeClass
    public static void init() {
        Configurations configs = new Configurations();
        try {
            config = configs.properties(new File(TEST_PROPERTIES_FILENAME));
        } catch (ConfigurationException e) {
            e.printStackTrace();
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
