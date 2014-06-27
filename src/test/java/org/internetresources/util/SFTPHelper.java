package org.internetresources.util;

import java.io.IOException;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.SSLException;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

public class SFTPHelper {
    private static Log LOG = LogFactory.getLog(SFTPHelper.class.getName());

    public static void main(String args[]) {
        String host = JOptionPane.showInputDialog("Enter hostname", "");
        int port = Integer.valueOf(JOptionPane.showInputDialog("Enter Port", "21"));
        String username = JOptionPane.showInputDialog("Enter username", "");
        String password = JOptionPane.showInputDialog("Enter password", "");
        SFTPHelper helper = new SFTPHelper();

        try {
            boolean isImplicit = false;
            FTPSClient ftpClient = helper.connect(host, port, username, password,
                    isImplicit);
            helper.listFiles(ftpClient);
            helper.close(ftpClient);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    public void close(FTPSClient ftpClient) throws IOException {
        LOG.debug("Logout");
        ftpClient.logout();

        LOG.debug("Disconnect");
        ftpClient.disconnect();
    }

    public FTPSClient connect(String host, int port, String username, String password) throws SocketException, SSLException, IOException {
        boolean isImplicit = false;
        return connect(host, port, username, password, isImplicit);
    }

    public FTPSClient connect(String host, int port, String username,
            String password, boolean isImplicit) throws SocketException,
            IOException, SSLException {
        FTPSClient ftpClient = new FTPSClient(isImplicit);
        LOG.debug("Connect to host");
        ftpClient.connect(host, port);
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            LOG.debug("FTP connect to host failed");
            return null;
        }
        LOG.debug("Login");
        if (!ftpClient.login(username, password)) {
            LOG.debug("FTP login failed");
            return null;
        }
        LOG.debug("Set protection buffer size");
        ftpClient.execPBSZ(0);
        LOG.debug("Set data channel protection to private");
        ftpClient.execPROT("P");
        LOG.debug("Enter local passive mode");
        ftpClient.enterLocalPassiveMode();
        return ftpClient;
    }

    public void listFiles(FTPSClient ftpClient) throws IOException {
        LOG.debug("list files");
        FTPFile[] lsFiles = ftpClient.listFiles();
        for (FTPFile f : lsFiles) {
            LOG.info(f.getName() + " "
                    + getDateString(f.getTimestamp().getTimeInMillis()));
        }
    }

    public static String getDateString(long timestamp) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return dateFormat.format(timestamp);
    }

    public FTPFile[] listFileByPrefix(FTPSClient ftpClient, final String filePath, final String filePrefix) throws IOException {
        return ftpClient.listFiles(filePath, new FTPFileFilter() {
            public boolean accept(FTPFile file) {
                String filename = file.getName();
                if (filename == null) {
                    return false;
                }
                return (filename.startsWith(filePrefix));
            }
           });
    }


    public FTPFile[] listYoungerFilesByPrefix(final int msgAgeMaxMinutes,
            FTPSClient ftpClient, final int serverTimeOffset,
            final String filePrefix, final String filePath) throws IOException {
        FTPFile[] resultFiles = ftpClient.listFiles(filePath, new FTPFileFilter() {
                public boolean accept(FTPFile file) {
                    String filename = file.getName();
                    if (filename == null) {
                        return false;
                    }
                    if (!filename.startsWith(filePrefix))  {
                        return false;
                    }
                    
                    long fileTS = file.getTimestamp().getTimeInMillis() + serverTimeOffset;
                    long nowMinusNMinutes = nowMinusNMinutes(msgAgeMaxMinutes);
                    boolean isYounger = (fileTS > nowMinusNMinutes);
                    return isYounger;
                }
            });
        return resultFiles;
    }

    private long nowMinusNMinutes(final int nMinutes) {
        long nowTime = (new Date()).getTime();
        int nMinInMs = nMinutes * 60000;
        return nowTime - nMinInMs;
    }
}
