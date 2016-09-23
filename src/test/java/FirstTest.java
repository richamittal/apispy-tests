import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;

/**
 * Created by richa.mittal on 2016-09-21.
 */
public class FirstTest {

    private static WebDriver driver;
    private static String username = "spy";
    private static String password = "dontspyhere";
    private static String baseUrl = "http://"+username+":"+password+"@apispy.io:3000";

    @BeforeClass
    public static void OpenBrowser(){
        System.setProperty("webdriver.chrome.driver", "C:\\ProjectInfo\\HelloSelenium\\Drivers\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get(baseUrl);
    }

    @AfterClass
    public static void quitBrowser() {
        driver.quit();
    }

    @Test
    public void MUSIsDisplayedTest() {
        //test if the method is displayed
        Assert.assertTrue(driver.findElement(By.id("method")).isDisplayed());
        //test if the URI is displayed
        Assert.assertTrue(driver.findElement(By.id("req-url")).isDisplayed());
        //test if the send button is displayed
        Assert.assertTrue(driver.findElement(By.id("sendButton")).isDisplayed());
    }

    @Test
    public void checkDefaultValuesTest() {
        //check the default value selected for method
        String defaultMethodSelected = driver.findElement(By.id("method")).getText();
        Assert.assertEquals("Default method", "GET", defaultMethodSelected);

        //check the default placeholder text for URI
        String defaultURIPlaceholderText = driver.findElement(By.id("req-url")).getAttribute("placeholder");
        Assert.assertEquals("Default placeholder value for URI", "http://petstore.swagger.io/#!/pet/findPetsByStatus", defaultURIPlaceholderText);
    }

    @Test
    public void submitDefaultValuesTest() throws InterruptedException {
        //keep the default values for method and URI and click the send button
        driver.findElement(By.id("sendButton")).click();
        //Wait for 1 second
        Thread.sleep(1000);
        //Get the response text
        String responseText = driver.findElement(By.className("panel-body")).getText();
        //System.out.println("text: "+responseText);
        //Assert that the response text contains API Spy
        Assert.assertTrue(responseText.contains("API Spy"));
    }

}


