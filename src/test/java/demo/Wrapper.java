package demo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Wrapper {
    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeClass
    public void initializedriver() {
        System.out.println("Constructor: TestCases");
        WebDriverManager.chromedriver().browserVersion("125.0.6422.142").setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(50));
    }

    @AfterClass
    public void endTest() {
        System.out.println("End Test: TestCases");
        driver.close();
        driver.quit();

    }

    public void navigate(String url) {
        driver.get(url);
    }

    public void clickElement(By locator) throws TimeoutException {

        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        element.click();
    }

    public void closebutton() {
        try {
            By closeButton = By.xpath("//span[@role='button' and text()='✕']");
            clickElement(closeButton);
        } catch (Exception e) {
            System.out.println("Login pop-up not found " + e.getMessage());
        }
    }

    public void sendText(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    private List<WebElement> findElements(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    private WebElement findElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void searchItem(String item) throws InterruptedException {

        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));
        searchBox.sendKeys(item);
        WebElement searchbutton = wait
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//button[contains(@class, '_2iLD__') and @type='submit']")));

        searchbutton.click();
        Thread.sleep(5000);

    }

    public void sortpopularity() {
        try {

            WebElement popularity = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()='Popularity']")));
            popularity.click();
        } catch (Exception e) {
            System.out.println("Encountered an exception while sorting by popularity:");
            e.printStackTrace();
        }
    }

    public int countrating() {

        List<WebElement> ratings = wait
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='XQDdHH']")));
        int count = 0;
        for (WebElement rating : ratings) {
            String ratingtext = rating.getText();
            if (!ratingtext.isEmpty()) {
                double rate = Double.parseDouble(ratingtext);
                if (rate <= 4.0) {
                    count++;
                }
            }
        }
        return count;

    }

    public void titlesanddiscount(double minDiscount) throws InterruptedException {

        List<WebElement> products = driver
                .findElements(By.xpath("//div[contains(@class, 'yKfJKb') and contains(@class, 'row')]"));

        for (WebElement product : products) {
            String title = "";
            String discountText = "";

            try {
                title = product.findElement(By.xpath(".//div[@class='KzDlHZ']")).getText();
            } catch (NoSuchElementException e) {
                System.out.println("Title element not found for a product!");
            }

            try {
                discountText = product.findElement(By.xpath(".//div[@class='UkUFwK']")).getText();
            } catch (NoSuchElementException e) {
                System.out.println("Discount element not found for a product!");
            }

            if (!discountText.isEmpty()) {
                try {

                    String[] parts = discountText.split("%");
                    if (parts.length == 2) {
                        int discountValue = Integer.parseInt(parts[0]);

                        if (discountValue > minDiscount) {
                            System.out.println("Title: " + title + ", Discount: " + discountValue + "%");
                        }
                    } else {
                        System.out.println("Invalid discount format for product: " + title);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Error in parsing the discount text for product: " + title + ". Discount text: " + discountText);
                }
            }

        }
    }

    public void coffeemugreview() throws InterruptedException, TimeoutException {
        try {
            WebElement checkbox = findElement(By.xpath("//div[text()='4★ & above']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", checkbox);
            Thread.sleep(2000);
        } catch (NoSuchElementException e) {
            System.out.println("Checkbox not found: " + e.getMessage());
            return;
        }
    
        List<WebElement> products = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='DOjaWF gdgoEp']//div[@class='slAVV4']")));
        List<Map<String, String>> sortedMugs = new ArrayList<>();
        Set<String> processedProducts = new HashSet<>();
    
        for (WebElement product : products) {
            try {
                String title = extractCoffeeTitle(product);
                if (processedProducts.contains(title)) continue;
                processedProducts.add(title);
    
                String imageUrl = extractCoffeeImageURL(product);
                String reviewText = extractCoffeeReviewText(product);
                int reviewCount = parseReviewCount(reviewText);
    
                Map<String, String> mugDetails = new HashMap<>();
                mugDetails.put("title", title);
                mugDetails.put("imageUrl", imageUrl);
                mugDetails.put("reviewCount", Integer.toString(reviewCount));
    
                addEntryToSortedMugs(sortedMugs, reviewCount, mugDetails);
    
            } catch (NoSuchElementException e) {
                System.out.println("Error in parsing the product: " + e.getMessage());
            }
        }
    
        System.out.println("Total products found: " + processedProducts.size());
        printTopReviews(sortedMugs);
    }
    
    private static String extractCoffeeTitle(WebElement product) {
        try {
            WebElement titleElement = product.findElement(By.xpath(".//a[contains(@class, 'wjcEIp')]"));
            return titleElement.getText();
        } catch (NoSuchElementException e) {
            System.out.println("Error finding coffee title: " + e.getMessage());
            return "";
        }
    }
    
    private static String extractCoffeeImageURL(WebElement product) {
        try {
            WebElement imageElement = product.findElement(By.xpath(".//img"));
            return imageElement.getAttribute("src");
        } catch (NoSuchElementException e) {
            System.out.println("Error in  finding the coffee image URL: " + e.getMessage());
            return "";
        }
    }
    
    private static String extractCoffeeReviewText(WebElement product) {
        try {
            WebElement reviewElement = product.findElement(By.xpath(".//span[@class='Wphh3N']"));
            return reviewElement.getText();
        } catch (NoSuchElementException e) {
            System.out.println("Error in finding the coffee review text: " + e.getMessage());
            return "";
        }
    }
    
    public int parseReviewCount(String reviewText) {
        try {
            String sanitizedText = reviewText.replaceAll("[()]", "").replaceAll(",", "");
            return Integer.parseInt(sanitizedText);
        } catch (NumberFormatException e) {
            System.out.println("Error in parsing the review count: " + e.getMessage());
            return 0;
        }
    }
    
    private static void addEntryToSortedMugs(List<Map<String, String>> sortedMugs, int reviewCount, Map<String, String> mugDetails) {
        sortedMugs.add(mugDetails);
        sortedMugs.sort((m1, m2) -> Integer.parseInt(m2.get("reviewCount")) - Integer.parseInt(m1.get("reviewCount")));
        if (sortedMugs.size() > 5) {
            sortedMugs.remove(5);
        }
    }
    
    private static void printTopReviews(List<Map<String, String>> sortedMugs) {
        for (Map<String, String> mugDetails : sortedMugs) {
            System.out.println("Review Count: " + mugDetails.get("reviewCount"));
            System.out.println("Title: " + mugDetails.get("title"));
            System.out.println("Image URL: " + mugDetails.get("imageUrl"));
            System.out.println("---------------");
        }
    }
}