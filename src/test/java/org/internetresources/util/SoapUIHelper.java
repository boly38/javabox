package org.internetresources.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eviware.soapui.tools.SoapUITestCaseRunner;

public class SoapUIHelper {
    private static Log LOG = LogFactory.getLog(SoapUIHelper.class.getName());

    public static void main(String[] args) {
        int appPort = 9010;
        SoapUIHelper soapuiHelper = new SoapUIHelper();
        
        String[] params = { "appPort=" + appPort };
        LOG.info("- appPort=:" + appPort);
        soapuiHelper.setSoapUiLog4jCustomConfig("target/test-classes/soapui-log4j.xml");
        // THEN
        LOG.info("play GETLOGIN_ONLY");
        try {
            soapuiHelper
                    .playSoapUIProject(
                            "src/test/resources/GETLOGIN_ONLY-soapui-project.xml",
                            params,
                            "target/");
        } catch (Exception e) {
            LOG.error(e);
        }
    }
    
    public void setSoapUiLog4jCustomConfig(String soapuiLog4jXmlFilename) {
        LOG.info("set SoapUI Log4j custom config : " + soapuiLog4jXmlFilename);
        // http://forum.soapui.org/viewtopic.php?f=13&t=17250&hilit=SoapUI+overwrites+and+closes+log4j+settings
        System.setProperty("soapui.log4j.config", soapuiLog4jXmlFilename);
    }

    public void playSoapUIProject(String soapUiXXmlFilename, String[] params, String outputFolder) throws Exception {
        LOG.info("play SoapUI Project : " + soapUiXXmlFilename);
        SoapUITestCaseRunner runner = new SoapUITestCaseRunner();
        runner.setProjectFile(soapUiXXmlFilename);
        runner.setProjectProperties(params);
        runner.setPrintReport(true);
        runner.setOutputFolder(outputFolder);
        runner.runRunner();
    }

}
