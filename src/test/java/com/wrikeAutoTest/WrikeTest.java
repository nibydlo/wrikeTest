package com.wrikeAutoTest;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import net.bytebuddy.utility.RandomString;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WrikeTest {

    private static WebDriver driver;

    @BeforeClass
    public static void setup() {

        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1440, 900));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // Open url: wrike.com;
        driver.get("https://wrike.com");

    }

    @Test
    public void test() {

        Random random = new Random();

        // Click "Login" button;
        driver.findElements(By.cssSelector(".wg-header__login-button.wg-btn.wg-btn--white.wg-btn--hollow")).get(1).click();

        // Click "Create account" link;
        driver.findElements(By.cssSelector(".link-content.w3link")).get(1).click();

        // Click "Get started for free" button near "Login" button;
        driver.findElements(By.cssSelector(".wg-header__free-trial-button.wg-btn.wg-btn--green")).get(2).click();

        // Fill in the email field with randomly generated value of email with mask “<random_text>+wpt@wriketask.qaa” (e.g. “abcdef+wpt@wriketask.qaa”);
        String email = RandomString.make(1 + random.nextInt(9)) + "+wpt@wriketask.qaa";
        driver.findElement(By.cssSelector(".wg-input.modal-form-trial__input")).sendKeys(email);

        // Click on "Create my Wrike account" button ...
        driver.findElement(By.cssSelector((".wg-btn.wg-btn--blue.modal-form-trial__submit"))).click();

        // waiting for complete downloading
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://www.wrike.com/resend/"));

        // + check with assertion that you are moved to the next page;
        Assert.assertEquals("https://www.wrike.com/resend/", driver.getCurrentUrl());

        // Fill in the Q&A section at the left part of the page (like randomly generated answers)

        int firstAnswer = random.nextInt(2);
        int secondAnswer = 2 + random.nextInt(5);
        int thirdAnswer = 7 + random.nextInt(3);

        List<WebElement> answers = driver.findElements(By.cssSelector(".switch__button"));
        answers.get(firstAnswer).click();
        answers.get(secondAnswer).click();
        answers.get(thirdAnswer).click();
        if (thirdAnswer == 9) {
            driver.findElement(By.cssSelector(".switch__input")).sendKeys("test");
        }

        // press submit butttom
        driver.findElement(By.cssSelector(".submit.wg-btn.wg-btn--navy.js-survey-submit")).click();

        //  + check with assertion that your answers are submitted;
        wait.until(ExpectedConditions.invisibilityOf(driver.findElements(By.cssSelector(".switch__button")).get(0)));
        Assert.assertFalse(driver.findElements(By.cssSelector(".switch__button")).get(0).isDisplayed());

        // Click on "Resend email"
        WebElement resendEmailButton = driver.findElements(By.cssSelector(".wg-btn.wg-btn--white.wg-btn--hollow.button.js-button")).get(0);
        resendEmailButton.click();

        // + check it with assertion;
        wait.until(ExpectedConditions.invisibilityOf(resendEmailButton));
        Assert.assertFalse(resendEmailButton.isDisplayed());

        // Check that section "Follow us" at the site footer contains the "Twitter" button that leads to the correct url and has the correct icon.
        WebElement twitterButton = driver.findElement(By.cssSelector(".wg-footer__group.wg-footer__group--social")).findElements(By.cssSelector(".wg-footer__social-link")).get(0);

        // check if it exist
        Assert.assertTrue(twitterButton.isDisplayed());

        // check if it leads to the correct url
        Assert.assertEquals("https://twitter.com/wrike", twitterButton.getAttribute("href"));

        // check if it contains right icon
        String imageAddress = twitterButton.findElement(By.tagName("use")).getAttribute("xlink:href");
        Assert.assertEquals("/content/themes/wrike/dist/img/sprite/vector/footer-icons.symbol.svg?v1#twitter", imageAddress);
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }
}
