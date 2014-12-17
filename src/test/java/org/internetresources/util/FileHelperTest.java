package org.internetresources.util;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;

public class FileHelperTest {

    @Test
    public void should_read_file() {
        // GIVEN
        FileHelper fHelper = new FileHelper();
        File f = new File("src/test/resources/fileTest.txt");

        // WHEN
        String readString = fHelper.readStringFromFile(f);

        // THEN
        assertThat(readString).isEqualTo("A_A01D48B80CE2\nA_A01D48B80CE2bis");
    }

}
