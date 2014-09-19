package org.internetresources.util;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
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
        
        seleniumHelper.navigateAndWaitAnUrl("http://127.0.0.1:9000");
        WebElement usernameField = driver.findElement(By.id("LoginUsername"));
        WebElement passwordField = driver.findElement(By.id("LoginPassword"));
        WebElement loginButton = driver.findElement(By.id("LoginSubmit"));
        usernameField.clear();
        usernameField.click();
        usernameField.sendKeys("toto");
        passwordField.clear();
        passwordField.click();
        passwordField.sendKeys("totopwd");
        loginButton.click();
        seleniumHelper.waitUntilTextIsPresent("loginNotificationDiv", "invalid username/password");
        driver.close(); // bye bye
    }

    /**
     * navigate to a giver url and wait for the page to be ready
     * @param targetUrl
     */
    public void navigateAndWaitAnUrl(String targetUrl) {
        LOG.info("navigate to => " + targetUrl);
        driver.get(targetUrl);
        waitForPageLoad();
        waitASecond(1);
    }

    public void waitASecond() {
        waitASecond(1);
    }
    public void waitASecond(int sec) {
        String action = "Wait " + sec + " second(s)";
        LOG.info(action);
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            LOG.error("error while " + action);
        }
    }
    public void waitForPageLoad() {
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
    }

    /**
     * wait for a given text into a given web element with id=elementId
     * @param elementId
     * @param txtMsg
     */
    public void waitUntilTextIsPresent(String elementId, String txtMsg) {
        waitDriver
            .ignoring(NoSuchElementException.class)
            .ignoring(StaleElementReferenceException.class)
            .pollingEvery(2, TimeUnit.SECONDS)
            .until(textIsPresent(elementId, txtMsg));
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
                  if (!isElementContainerDisplayed) {
                      LOG.debug("\t#" + elemId + " not displayed");
                  }
                  if (elementContainerText != null) {
                      LOG.debug("\t#" + elemId + " content:" +  elementContainerText);
                  }
                }
                return textIsPresent;
            }
        };
    }

    public void waitUntilClassIsPresent(String elementId, String classToBe, int customTimeoutSec, int customPollingIntervalSec) {
        WebDriverWait customWaitDriver = new WebDriverWait(driver, customTimeoutSec);
        customWaitDriver
        .ignoring(NoSuchElementException.class)
        .ignoring(StaleElementReferenceException.class)
        .pollingEvery(customPollingIntervalSec, TimeUnit.SECONDS)
        .until(classIsPresent(elementId, classToBe));
    }
    public void waitUntilClassIsPresent(String elementId, String classToBe) {
        waitDriver
        .ignoring(NoSuchElementException.class)
        .ignoring(StaleElementReferenceException.class)
        .pollingEvery(2, TimeUnit.SECONDS)
        .until(classIsPresent(elementId, classToBe));
    }
    protected Function<WebDriver, Boolean> classIsPresent(String elementId, String classToBe){
        final String elemId = elementId;
        final String clzz = classToBe;
        return new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                WebElement elementContainer = driver.findElement(By.id(elemId));
                return isClassPresent(elemId, clzz, elementContainer);
            }
        };
    }

    private boolean isClassPresent(final String elemId,
            final String clzz, WebElement elementContainer) {
        String[] elemClzzs = (elementContainer != null && elementContainer.getAttribute("class") != null) 
                                ? elementContainer.getAttribute("class").split(" ")
                                : null;
        boolean classIsPresent = elementContainer != null
                             && Arrays.asList(elemClzzs).contains(clzz);
        if (!classIsPresent) {
          LOG.debug("'" + clzz + "' is NOT present into #" + elemId);
        }
        return classIsPresent;
    }

    public void updateInputField(String fieldId, String fieldNewValue) {
        LOG.debug("#" + fieldId + "=" + fieldNewValue);
        WebElement fieldToUpdate = driver.findElement(By.id(fieldId));
        fieldToUpdate.clear();
        fieldToUpdate.click();
        fieldToUpdate.sendKeys(fieldNewValue);
    }

    public void clickOnElement(String elementId) {
        LOG.debug("click #" + elementId);
        WebElement targetElement = driver.findElement(By.id(elementId));
        targetElement.click();
    }
    public void assertInputField(String inputFieldId, String expectedValue) {
        WebElement fieldElement = driver.findElement(By.id(inputFieldId));
        String fieldCurValue = fieldElement.getAttribute("value");
        expectedValue = expectedValue != null ? expectedValue : "";
        if (!expectedValue.equals(fieldCurValue)) {
            String assertInputError = "#" + inputFieldId + " value=" + fieldCurValue + " expected=" + expectedValue;
            LOG.error(assertInputError);
            throw new AssertionError(assertInputError);
        }
    }
    public void assertClassPresent(String elementId,
            String expectedClassId) {
        WebElement targetElement = driver.findElement(By.id(elementId));
        boolean classPresent = isClassPresent(elementId, expectedClassId, targetElement);
        if (!classPresent) {
            String assertInputError = "#" + elementId + " class=" + expectedClassId + " expected";
            LOG.error(assertInputError);
            throw new AssertionError(assertInputError);
        }
    }
}
