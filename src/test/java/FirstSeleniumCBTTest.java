/**
 * Created by richa.mittal on 2016-10-14.
 */

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ThresholdingOutputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;


@RunWith(Parallelized.class)
public class FirstSeleniumCBTTest {
    private String username = "richa.mittal%40smartbear.com";
    private String authkey = "u0caf2eae0f3bbdd";
    private String os;
    private String browser;
    private String screenResolution;
    private RemoteWebDriver driver;

    @Parameterized.Parameters
    public static Collection environments(){
        return Arrays.asList(new Object[][]{
                { "Win7x64-C1", "Chrome53x64", "1024x768"  },
                { "Win7x64-C1", "FF46x64", "1024x768"  },
                { "Mac10.12", "Safari10", "1024x768"  }
        });
    }

    public FirstSeleniumCBTTest(String os, String browser, String screenResolution){
        this.os = os;
        this.browser = browser;
        this.screenResolution = screenResolution;
    }

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("name", "ApiSpy Test");
        caps.setCapability("build", "1.0");
        caps.setCapability("os_api_name", os);
        caps.setCapability("browser_api_name", browser);
        caps.setCapability("screen_resolution", screenResolution);
        caps.setCapability("record_video", "true");
        caps.setCapability("record_network", "false");
        System.out.println("Testing on: " + this.os + " , " + this.browser + " , " + this.screenResolution);
        driver = new RemoteWebDriver(new URL("http://" + username + ":" + authkey + "@hub.crossbrowsertesting.com:80/wd/hub"), caps);
        System.out.println(driver.getSessionId());
    }

    @After
    public void tearDown(){
           driver.quit();
    }

    @Test
    public void MUSIsDisplayedTest() throws Exception {
        driver.get("http://spy:dontspyhere@apispy.io");

        //test if the method is displayed
        Assert.assertTrue(driver.findElement(By.id("method")).isDisplayed());

        //test if the URI is displayed
        Assert.assertTrue(driver.findElement(By.id("req-url")).isDisplayed());

        //test if the send button is displayed
        Assert.assertTrue(driver.findElement(By.id("sendButton")).isDisplayed());

        //take a screenshot
        driver = (RemoteWebDriver) new Augmenter().augment(driver);
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(srcFile, new File("Screenshot.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkDefaultValuesTest() {
        driver.get("http://spy:dontspyhere@apispy.io");

        //check the default value selected for method
        String defaultMethodSelected = driver.findElement(By.id("method")).getText();
        Assert.assertEquals("Default method", "GET", defaultMethodSelected);

        //check the default placeholder text for URI
        String defaultURIPlaceholderText = driver.findElement(By.id("req-url")).getAttribute("placeholder");
        Assert.assertEquals("Default placeholder value for URI", "http://petstore.swagger.io/#!/pet/findPetsByStatus", defaultURIPlaceholderText);
    }

    @Test
    public void submitDefaultValuesTest() throws InterruptedException {
        driver.get("http://spy:dontspyhere@apispy.io");
        System.out.println("Entering URL");
        driver.findElement(By.id("req-url")).sendKeys("http://petstore.swagger.io/#!/pet/findPetsByStatus");
        //keep the default values for method and URI and click the send button
        System.out.println("Clicking Send");
        driver.findElement(By.id("sendButton")).click();
        //Wait for 1 second
        Thread.sleep(2000);
        //Get the response text
        String responseText = driver.findElement(By.className("panel-body")).getText();
        System.out.println("Got response");
        //System.out.println("text: "+responseText);
        Assert.assertTrue(responseText.contains("Swagger UI"));
    }
}
