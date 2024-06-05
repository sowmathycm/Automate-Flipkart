package demo;

import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TestCases {

    private Wrapper wrapper;
    ChromeDriver driver;

    @BeforeClass
    public void setup() {
        System.out.println("Constructor: TestCases");
        wrapper = new Wrapper();
        wrapper.initializedriver();
        // WebDriverManager.chromedriver().browserVersion("125.0.6422.142").setup();
        // driver = new ChromeDriver();
        // driver.manage().window().maximize();
    }

    @AfterClass
    public void endTest() {

        wrapper.endTest();

    }

    @Test
    public void testCase01() throws InterruptedException {
        System.out.println("Start Test case: testCase01");
        wrapper.navigate("https://www.flipkart.com/");
        wrapper.searchItem("Washing Machine");
        wrapper.sortpopularity();
        Thread.sleep(2000);
        int count = wrapper.countrating();
        System.out.println("Count of items with rating <= 4 stars:" + count);
        System.out.println("End Test case: testCase01");

    }

    @Test
    public void testCase02() throws InterruptedException {

        System.out.println("Start Test case: testCase02");
        wrapper.navigate("https://www.flipkart.com/");
        wrapper.closebutton();
        wrapper.navigate("https://www.flipkart.com/");
        wrapper.searchItem("iPhone");
        Thread.sleep(2000);
        wrapper.titlesanddiscount(17.0);

    }

    @Test

    public void testCase03() throws InterruptedException, TimeoutException {

        System.out.println("Start Test case: testCase03");
        wrapper.navigate("https://www.flipkart.com/");
        wrapper.searchItem("Coffee Mug");
        Thread.sleep(2000);
        wrapper.coffeemugreview();
    }

}
