package com.github.funthomas424242.jpacker4gramps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileHelperTest {

    private static final String TEST_PROPERTIES_FILENAME = "test.properties";

    private static final String BEISPIEL1_GRAMPS_FILE = "beispiel1.gramps.file";
    private static final String BEISPIEL1_GRAMPS_FILE_LEN = "beispiel1.gramps.file.len";

    protected FileHelper fileHelper;

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
        final File file = new File(config.getString(BEISPIEL1_GRAMPS_FILE));
        assertTrue("gramps.file does not exists:", file.exists());
        fileHelper = new FileHelper(file);
    }

    @Test
    public void readContentOfDatabaseFile() throws IOException {
        final String content = fileHelper.getFileContent();
        assertEquals(config.getInt(BEISPIEL1_GRAMPS_FILE_LEN),
                content.length());
    }

}
