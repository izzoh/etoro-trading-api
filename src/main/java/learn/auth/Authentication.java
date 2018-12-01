package learn.auth;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class Authentication {

    @Autowired
    WebDriver driver;

    @Autowired
    ApplicationArguments applicationArguments;

    public void login() {
        String args[] = applicationArguments.getSourceArgs();
        driver.get("https://www.etoro.com/portfolio");

        driver.findElement(By.id("username")).sendKeys(args[0]);
        driver.findElement(By.id("password")).sendKeys(args[1]);

        driver.findElement(By.className("e-btn-big")).click();

        System.out.println();
    }
}