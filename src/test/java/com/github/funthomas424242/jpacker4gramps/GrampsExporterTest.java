//package com.github.funthomas424242.jpacker4gramps;
//
//import static org.junit.Assert.assertEquals;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//import org.apache.commons.configuration2.Configuration;
//import org.apache.commons.configuration2.builder.fluent.Configurations;
//import org.apache.commons.configuration2.ex.ConfigurationException;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.github.funthomas424242.jpacker4gramps.GrampsExporter;
//
//public class GrampsExporterTest {
//
//    private static final String TEST_PROPERTIES_FILENAME = "test.properties";
//
//    protected GrampsExporter exporter;
//
//    protected static Configuration config;
//
//    @BeforeClass
//    public static void init() {
//        Configurations configs = new Configurations();
//        try {
//            config = configs.properties(new File(TEST_PROPERTIES_FILENAME));
//        } catch (ConfigurationException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Before
//    public void setUp() {
//        final File grampsDatabasFile = new File(
//                "src/test/resources/beispiel1/database/ahnen1.txt");
//
//        final File targetArchive = new File("target/test/archive1");
//        exporter = new GrampsExporter(grampsDatabasFile, mediaFolder,
//                targetArchive);
//    }
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
//
//}
