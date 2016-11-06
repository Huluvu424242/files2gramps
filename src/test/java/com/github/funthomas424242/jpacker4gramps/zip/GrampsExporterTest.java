//package com.github.funthomas424242.jpacker4gramps.zip;
//
//import static org.junit.Assert.assertEquals;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import com.github.funthomas424242.jpacker4gramps.GrampsExporter;
//
//public class GrampsExporterTest {
//
//    protected GrampsExporter exporter;
//
//    @Before
//    public void setUp() {
//        final File grampsDatabasFile = new File(
//                "src/test/resources/beispiel1/database/ahnen1.txt");
//        final File mediaFolder = new File("src/test/resources/bespiel1/media");
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
