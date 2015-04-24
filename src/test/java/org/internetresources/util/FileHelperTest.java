package org.internetresources.util;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Test;

public class FileHelperTest {

    @Test
    public void should_read_file() throws FileNotFoundException {
        // GIVEN
        FileHelper fHelper = spy(new FileHelper());
        File f = new File("src/test/resources/fileTest.txt");

        // WHEN
        String readString = fHelper.readStringFromFile(f);

        // THEN
        verify(fHelper)._getReadRandomAccessFile(f);
        assertThat(readString).isEqualTo("A_A01D48B80CE2\nA_A01D48B80CE2bis");
    }

    @Test
    public void should_return_null_on_read_file_exception() throws IOException {
        // GIVEN
        FileHelper fHelper = spy(new FileHelper());
        File f = new File("src/test/resources/fileTest.txt");
        RandomAccessFile raf = mock(RandomAccessFile.class);
        doReturn(raf).when(fHelper)._getReadRandomAccessFile(f);
        doThrow(new IOException("c'est balot")).when(raf).read(any(byte[].class));

        // WHEN
        String readString = fHelper.readStringFromFile(f);

        // THEN
        verify(fHelper)._getReadRandomAccessFile(f);
        assertThat(readString).isNull();
        verify(raf).close();
    }
}
