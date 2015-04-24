package org.internetresources.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileHelper {
    private static Log LOG = LogFactory.getLog(FileHelper.class.getName());
    final String READ_MODE = "r";

    RandomAccessFile _getReadRandomAccessFile(File file) throws FileNotFoundException {
        return new RandomAccessFile(file, READ_MODE);
    }

    public String readStringFromFile(File file) {
        String line = null;
        RandomAccessFile raf = null;
        try {
            raf = _getReadRandomAccessFile(file);
            byte[] buffer = new byte[(int) raf.length()];
            raf.read(buffer);
            line = new String(buffer);
        } catch (IOException e) {
            LOG.error("IOException while reading "+ file.getAbsolutePath());
            return null;
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }

}
