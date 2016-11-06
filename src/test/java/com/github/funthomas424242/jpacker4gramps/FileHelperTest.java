package com.github.funthomas424242.jpacker4gramps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class FileHelperTest {

    protected FileHelper fileHelper;

    @Before
    public void setUp() {
        final File file = new File(
                "src/test/resources/beispiel1/database/ahnen1.txt");
        assertTrue(file.exists());
        fileHelper = new FileHelper(file);
    }

    @Test
    public void readContentOfDatabaseFile() throws IOException {
        final String content = fileHelper.getFileContent();
        assertEquals(28, content.length());
    }

}
