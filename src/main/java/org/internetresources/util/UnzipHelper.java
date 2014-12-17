package org.internetresources.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.ant.compress.taskdefs.Unzip;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UnzipHelper {
    private static Log LOG = LogFactory.getLog(UnzipHelper.class.getName());
    
    /**
     * unzip a file into a directory requirement: Windows OS or Linux OS
     * 
     * @param zipFile
     *            file to unzip
     * @param outputFolder
     *            target output directory
     * @throws Exception 
     */
    public void unzip(String zipFile, String outputFolder) throws Exception {
        // windows
        if (SystemHelper.isWindows()) {
            try {
                antUnzip(zipFile, outputFolder);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                throw e;
            }
            return;
        }

        // linux
        if (!SystemHelper.isUnix()) {
            throw new Exception("Only unix and windows are supported");
        }
        try {
            linuxUnzip(zipFile, outputFolder);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Unzip it src
     * http://www.mkyong.com/java/how-to-decompress-files-from-a-zip-file/
     * 
     * @param zipFile
     *            input zip file
     * @param output
     *            zip file output folder
     * 
     *            linux: this method dont keep file permission !
     *            http://stackoverflow.com/questions/1050560/maintain-file-permissions-when-extracting-from-a-zip-file-using-jdk-5-api
     */
    public void unZipIt(String zipFile, String outputFolder) {

        long unzippedFile = 0;
        long unzippedDir = 0;
        byte[] buffer = new byte[1024];

        try {

            // create output directory is not exists
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }

            // get the zip file content
            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(zipFile));
            // get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator
                        + fileName);
                if (ze.isDirectory()) {
                    // LOG.info("dir unzip : " + newFile.getAbsoluteFile());
                    newFile.mkdirs();
                    unzippedDir++;
                    ze = zis.getNextEntry();
                    continue;
                }

                // LOG.info("file unzip : " + newFile.getAbsoluteFile());
                unzippedFile++;

                // create all non exists folders
                // else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                if (SystemHelper.isUnix() && fileName.endsWith(".sh")) {
                    newFile.setExecutable(true);
                }
                if (SystemHelper.isWindows() && fileName.endsWith(".bat")) {
                    newFile.setExecutable(true);
                }
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            LOG.info("Unzip Done : " + unzippedFile + " file(s) and "
                    + unzippedDir + " dir(s)");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * pom dep : <dependency> <groupId>org.apache.ant</groupId>
     * <artifactId>ant-compress</artifactId> <version>1.2</version>
     * <scope>test</scope> </dependency>
     * 
     * @param zipFile
     * @param outputFolder
     *            linux: this method dont keep file permission !
     */
    public void antUnzip(String zipFile, String outputFolder) {
        org.apache.ant.compress.taskdefs.Unzip u = new Unzip();
        u.setSrc(new File(zipFile));
        u.setDest(new File(outputFolder));
        u.execute();
    }

    /**
     * require OS "unzip" command (in $path)
     * 
     * @param zipFile
     * @param outputFolder
     * @throws Exception e
     * 
     * src:
     *  http://stackoverflow.com/questions/5483830/process-waitfor-never-returns
     */
    public void linuxUnzip(String zipFile, String outputFolder) throws Exception {
        try {
            String unzipCmd = "/usr/bin/unzip";
            String command = unzipCmd + " " + zipFile + " -d " + outputFolder + " -qq";
            LOG.info(command);
            Process process = new ProcessBuilder(unzipCmd, zipFile, "-d",
                   outputFolder, "-qq").redirectErrorStream(true).start();
            // old method // Process process = Runtime.getRuntime().exec(command);
            BufferedReader stdOutReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line= null;
            while ((line = stdOutReader.readLine()) != null) {
                LOG.info(line);
            }
            stdOutReader.close();
            process.waitFor();
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
            throw e;
        }
    }
}
