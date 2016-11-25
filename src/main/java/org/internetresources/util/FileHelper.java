package org.internetresources.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileHelper {
    private static Log LOG = LogFactory.getLog(FileHelper.class.getName());
    final String READ_MODE = "r";

    RandomAccessFile _getReadRandomAccessFile(File file) throws FileNotFoundException {
        return new RandomAccessFile(file, READ_MODE);
    }

    public byte[] readByteFromFile(File file) {
        RandomAccessFile raf = null;
        try {
            raf = _getReadRandomAccessFile(file);
            byte[] buffer = new byte[(int) raf.length()];
            raf.read(buffer);
            return buffer;
        } catch (IOException e) {
        	String exMsg = String.format("IOException while reading %s", file.getAbsolutePath());
            LOG.error(exMsg);
            return null;
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public String readStringFromFile(File file) {
        byte[] buffer = readByteFromFile(file); // new byte[(int) raf.length()];
        if (buffer == null) {
            return null;
        }
        return new String(buffer);
    }

    
    // save uploaded file to a defined location on the server
    public File saveFile(InputStream uploadedInputStream, File tempFile) {
        OutputStream outpuStream = null;
        try {
            int read = 0;
            byte[] bytes = new byte[1024];

            outpuStream = new FileOutputStream(tempFile);
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outpuStream.write(bytes, 0, read);
            }
            outpuStream.flush();
            return tempFile;
        } catch (IOException e) {
        	String exMsg = String.format("unable to create temp file from upload: %s", e.getMessage());
        	LOG.error(exMsg, e);
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
            return null;
        } finally {
            if (outpuStream != null) {
                try {
                    outpuStream.close();
                } catch (IOException e) {
                	String excMsg = String.format("unable to close stream from upload: %s", e.getMessage());
                	LOG.error(excMsg, e);
                }
            }
        }
    }

	public void saveToFile(String fileName, byte[] fileContent) throws IOException {
		FileOutputStream file = new FileOutputStream(fileName);
		try {
			if (fileContent != null) {
				file.write(fileContent);
			}
		} finally {
			file.close();			
		}
	}
}
