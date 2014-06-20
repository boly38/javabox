package org.internetresources.util;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.google.common.base.Function;

/**
 * TIP angluarjs + htmlunit
 * http://stackoverflow.com/questions/20153104/htmlunit-not-working-with-angularjs
 * using
<html id="ng-app" class="ng-app: appmodule;"> 
 * instead of
<html ng-app="appmodule">
 * 
 * TIP wait handlers example :
 * src: http://stackoverflow.com/questions/5868439/wait-for-page-load-in-selenium
 *
 */
public class SeleniumHelper {
    private static Log LOG = LogFactory.getLog(SeleniumHelper.class.getName());

    private int maxWaitInSeconds = 30;
    private WebDriver driver;
    private WebDriverWait waitDriver;

    public SeleniumHelper(WebDriver driver, int maxWaitSeconds) {
        init(driver, maxWaitSeconds);
    }
    public SeleniumHelper(WebDriver driver) {
        init(driver, 30);
    }

    private void init(WebDriver driver, int maxWaitSeconds) {
        this.driver = driver;
        this.maxWaitInSeconds = maxWaitSeconds;
        this.waitDriver = new WebDriverWait(driver, maxWaitInSeconds);
    }

    /**
     * sample snapshot on how to use this helper
     */
    public static void usageExample() {
        final int MAX_WAIT_SEC = 30;
        WebDriver driver = new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER_11);
        ((HtmlUnitDriver) driver).setJavascriptEnabled(true);
        // time the browser should wait to find an element
        driver.manage().timeouts().implicitlyWait(MAX_WAIT_SEC, TimeUnit.SECONDS);
        SeleniumHelper seleniumHelper = new SeleniumHelper(driver, MAX_WAIT_SEC);
        // go to the default page : assume login
        seleniumHelper.navigateAndWaitAnUrl("http://127.0.0.1:9000");
        // get login page web element (with implicitlyWait)
        WebElement usernameField = driver.findElement(By.id("LoginUsernameId"));
        WebElement passwordField = driver.findElement(By.id("LoginPasswordId"));
        WebElement loginButton = driver.findElement(By.id("LoginSubmitId"));
        // set login username and password and submit
        usernameField.sendKeys("toto");
        passwordField.sendKeys("totopwd");
        loginButton.click();
        // wait the error using waitDriver 
        seleniumHelper.waitUntilTextIsPresent("loginNotificationDiv", "invalid username/password");
        // close the browser
        driver.close();
    }
    
    
    /**
     * navigate to a giver url and wait for the page to be ready
     * @param targetUrl
     */
    public void navigateAndWaitAnUrl(String targetUrl) {
        LOG.info("navigate to => " + targetUrl);
        driver.get(targetUrl);
        waitForPageLoad(maxWaitInSeconds);
    }

    public void waitForPageLoad(int waitTimeoutSec) {
        waitDriver.until(new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                String windowsState = String
                        .valueOf(((JavascriptExecutor) driver)
                                .executeScript("return document.readyState"));
                boolean isComplete = windowsState.equals("complete");
                if (!isComplete) {
                    LOG.debug("waitForPageLoad; current Window State: "
                            + windowsState);
                }
                return isComplete;
            }
        });
        waitForJavascript();
    }

    private void waitForJavascript() {
        LOG.debug("javascript wait 500ms");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }

    /**
     * wait for a given text into a given web element with id=elementId
     * @param elementId
     * @param errorMsg
     */
    public void waitUntilTextIsPresent(String elementId, String errorMsg) {
        waitDriver.until(textIsPresent(elementId, errorMsg));
    }
    protected Function<WebDriver, Boolean> textIsPresent(String elementId, String text){
        final String elemId = elementId;
        final String txt = text;
        return new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                WebElement elementContainer = driver.findElement(By.id(elemId));
                boolean isElementContainerDisplayed = (elementContainer != null ? elementContainer.isDisplayed() : false);
                String elementContainerText = (elementContainer != null ? elementContainer.getText() : null);
                boolean textIsPresent = elementContainer != null
                                     && isElementContainerDisplayed
                                     && elementContainerText != null 
                                     && elementContainerText.contains(txt);
                if (!textIsPresent) {
                  LOG.debug("wait textIsPresent'" + txt + "' is NOT present into #" + elemId);
                }
                return textIsPresent;
            }
        };
    }

}
