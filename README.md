JavaBox
=======

Java helper and tips

* [HexAsciiConversion helper](https://github.com/boly38/javabox/blob/master/src/main/java/org/internetresources/util/HexAsciiConversion.java)
* [File helper](https://github.com/boly38/javabox/blob/master/src/main/java/org/internetresources/util/FileHelper.java)
* [Properties helper](https://github.com/boly38/javabox/blob/master/src/main/java/org/internetresources/util/PropertiesHelper.java)
* [Selenium helper](https://github.com/boly38/javabox/blob/master/src/main/java/org/internetresources/util/SeleniumHelper.java)
* [sFTP helper](https://github.com/boly38/javabox/blob/master/src/main/java/org/internetresources/util/SFTPHelper.java)
* [SoapUI helper](https://github.com/boly38/javabox/blob/master/src/main/java/org/internetresources/util/SoapUIHelper.java)
* [System helper](https://github.com/boly38/javabox/blob/master/src/main/java/org/internetresources/util/SystemHelper.java)
* [UnsafeSSL helper](https://github.com/boly38/javabox/blob/master/src/main/java/org/internetresources/util/UnsafeSSLHelper.java)
* [Unzip helper](https://github.com/boly38/javabox/blob/master/src/main/java/org/internetresources/util/UnzipHelper.java)

SSL certificates
----------------

When you're trying to request a remote secure host without a complete and valid certification chain you often get the following error :
<pre>
sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
</pre>

This project also includes <code>InstallCert</code> & <code>InstallCertProxy</code> to auto-detect ssl certificates of a SSL target host.

Example to append intermediate certificates (PKIX chain) to your truststore:
<pre>
   java -Dhttps.proxyHost=proxy -Dhttps.proxyPort=80 -Djava.home=%JAVA_HOME%\jre -cp target\javabox-1.0-SNAPSHOT.jar InstallCertProxy myhttpstarget
</pre>

To list all ssl certificates:
<pre>
   keytool -list -keystore "%JAVA_HOME%/jre/lib/security/cacerts"
</pre>

If you didn't success to fix your certification path (and for test only!) you could also use [UnsafeSSL helper](https://github.com/boly38/javabox/blob/master/src/main/java/org/internetresources/util/UnsafeSSLHelper.java) to disable all SSL check.


NB: any remarks or pull request are welcome...
