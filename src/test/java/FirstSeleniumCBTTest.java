/**
 * Created by richa.mittal on 2016-10-14.
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@RunWith(Parameterized.class)
public class FirstSeleniumCBTTest {
    String username = "richa.mittal%40smartbear.com"; // Your username
    String authkey = "u0caf2eae0f3bbdd";  // Your authkey
    String testScore = "unset";
    RemoteWebDriver driver;

    private String os;
    private String browser;
    private String screenResolution;

    @Before
    public void setUp() throws MalformedURLException {

        DesiredCapabilities caps = setCapabilities();
        openAPISpyOnCBT(caps);
    }
    @After
    public void tearDown(){
          // quit the driver
           driver.quit();
    }

    public FirstSeleniumCBTTest(String os, String browser, String screenResolution){
        this.os = os;
        this.browser = browser;
        this.screenResolution = screenResolution;
    }

    @Parameterized.Parameters
    public static Collection environments(){
        return Arrays.asList(new Object[][]{
                { "Win7x64-C1", "Chrome53x64", "1024x768"  },
                { "Win7x64-C1", "FF46x64", "1024x768"  },
                { "Win10", "Edge20", "1024x768"  },
                { "Mac10.12", "Safari10", "1024x768"  }
        });
    }

    @Test
    public void MethodIsDisplayedTest() throws Exception {

        try {

            //test if the method is displayed
            Assert.assertTrue(driver.findElement(By.id("method")).isDisplayed());

            // if we get to this point, then all the assertions have passed
            // that means that we can set the score to pass in our system
             testScore = "pass";
        } catch (AssertionError ae) {

            // if we have an assertion error, take a snapshot of where the test fails
            // and set the score to "fail"
            String snapshotHash = takeSnapshot(driver.getSessionId().toString());
            setDescription(driver.getSessionId().toString(), snapshotHash, ae.toString());
            testScore = "fail";
        } finally {

            System.out.println("Test complete: " + testScore);

            // here we make an api call to actually send the score
            setScore(driver.getSessionId().toString(), testScore);
        }
    }


      @Test
      public void URLIsDisplayedTest() throws Exception {
          try {
              //test if the URL is displayed
              Assert.assertTrue(driver.findElement(By.id("req-url")).isDisplayed());
              // if we get to this point, then all the assertions have passed
              // that means that we can set the score to pass in our system
              testScore = "pass";
          } catch (AssertionError ae) {
              // if we have an assertion error, take a snapshot of where the test fails
              // and set the score to "fail"
              String snapshotHash = takeSnapshot(driver.getSessionId().toString());
              setDescription(driver.getSessionId().toString(), snapshotHash, ae.toString());
              testScore = "fail";
          } finally {
              System.out.println("Test complete: " + testScore);
              // here we make an api call to actually send the score
              setScore(driver.getSessionId().toString(), testScore);
          }
      }

      @Test
      public void SendIsDisplayedTest() throws Exception {
         try {
              //test if the send button is displayed
              Assert.assertTrue(driver.findElement(By.id("sendButton")).isDisplayed());
              // if we get to this point, then all the assertions have passed
              // that means that we can set the score to pass in our system
              testScore = "pass";
         } catch (AssertionError ae) {

              // if we have an assertion error, take a snapshot of where the test fails
              // and set the score to "fail"
              String snapshotHash = takeSnapshot(driver.getSessionId().toString());
              setDescription(driver.getSessionId().toString(), snapshotHash, ae.toString());
              testScore = "fail";
         } finally {

              System.out.println("Test complete: " + testScore);

              // here we make an api call to actually send the score
              setScore(driver.getSessionId().toString(), testScore);
         }
      }

    public DesiredCapabilities setCapabilities (){
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("name", "ApiSpy Test");
        caps.setCapability("build", "1.0");
        caps.setCapability("os_api_name", os);
        caps.setCapability("browser_api_name", browser);
        caps.setCapability("screen_resolution", screenResolution);
        caps.setCapability("record_video", "true");
        caps.setCapability("record_network", "false");
        return caps;
    }

    public void openAPISpyOnCBT(DesiredCapabilities caps) throws MalformedURLException {
        driver = new RemoteWebDriver(new URL("http://" + username + ":" + authkey + "@hub.crossbrowsertesting.com:80/wd/hub"), caps);
        System.out.println(driver.getSessionId());
        String apispy_username = "spy";
        String apispy_password = "dontspyhere";
        String baseUrl = "http://"+apispy_username+":"+apispy_password+"@apispy.io:3000";
        driver.get(baseUrl);
        System.out.println("Loading Url");
        driver.get("http://spy:dontspyhere@apispy.io:3000");
    }

    public JsonNode setScore(String seleniumTestId, String score) throws UnirestException {
        // Mark a Selenium test as Pass/Fail
        HttpResponse<JsonNode> response = Unirest.put("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .field("action","set_score")
                .field("score", score)
                .asJson();
        return response.getBody();
    }

    public String takeSnapshot(String seleniumTestId) throws UnirestException {
        /*
         * Takes a snapshot of the screen for the specified test.
         * The output of this function can be used as a parameter for setDescription()
         */
        HttpResponse<JsonNode> response = Unirest.post("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}/snapshots")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .asJson();
        // grab out the snapshot "hash" from the response
        String snapshotHash = (String) response.getBody().getObject().get("hash");

        return snapshotHash;
    }

    public JsonNode setDescription(String seleniumTestId, String snapshotHash, String description) throws UnirestException{
        /*
         * sets the description for the given seleniemTestId and snapshotHash
         */
        HttpResponse<JsonNode> response = Unirest.put("http://crossbrowsertesting.com/api/v3/selenium/{seleniumTestId}/snapshots/{snapshotHash}")
                .basicAuth(username, authkey)
                .routeParam("seleniumTestId", seleniumTestId)
                .routeParam("snapshotHash", snapshotHash)
                .field("description", description)
                .asJson();
        return response.getBody();
    }

}
