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

    protected FileHelper fileHelper;

    protected static Configuration config;

    @BeforeClass
    public static void init() {
        Configurations configs = new Configurations();
        try {
            config = configs.properties(new File("test.properties"));
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        final File file = new File(config.getString("gramps.file"));
        assertTrue("gramps.file does not exists:", file.exists());
        fileHelper = new FileHelper(file);
    }

    @Test
    public void readContentOfDatabaseFile() throws IOException {
        final String content = fileHelper.getFileContent();
        assertEquals(28, content.length());
    }

}
