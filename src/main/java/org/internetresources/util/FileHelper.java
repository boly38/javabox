package org.internetresources.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileHelper {
    final String READ_MODE = "r";

    public String readStringFromFile(File file) {
        String line = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, READ_MODE);
            byte[] buffer = new byte[(int) raf.length()];
            raf.read(buffer);
            line = new String(buffer);
        } catch (IOException e) {
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
