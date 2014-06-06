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
import org.junit.Assume;

public class UnzipHelper {
    private static Log LOG = LogFactory.getLog(UnzipHelper.class.getName());
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS
                .indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }

    public static String getOS() {
        return OS;
    }

    /**
     * unzip a file into a directory requirement: Windows OS or Linux OS
     * 
     * @param zipFile
     *            file to unzip
     * @param outputFolder
     *            target output directory
     */
    public void unzip(String zipFile, String outputFolder) {
        // windows
        if (isWindows()) {
            antUnzip(zipFile, outputFolder);
            return;
        }

        // linux
        Assume.assumeTrue("Only unix and windows are supported", isUnix());
        try {
            linuxUnzip(zipFile, outputFolder);
        } catch (Exception e) {
            Assume.assumeNoException(e);
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
                if (isUnix() && fileName.endsWith(".sh")) {
                    newFile.setExecutable(true);
                }
                if (isWindows() && fileName.endsWith(".bat")) {
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
     * @throws IOException
     * @throws InterruptedException
     */
    public void linuxUnzip(String zipFile, String outputFolder)
            throws IOException, InterruptedException {
        Process process = new ProcessBuilder("unzip", zipFile, "-d",
                outputFolder).start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
        while ((reader.readLine()) != null) {
        }
        process.waitFor(); // http://stackoverflow.com/questions/5483830/process-waitfor-never-returns
    }
}
