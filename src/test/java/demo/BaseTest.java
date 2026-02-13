package demo;

import io.github.bonigarcia.wdm.WebDriverManager;
import it.univr.track.SmartTrackApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SmartTrackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach // Changed from @Before
    public void setUp() {
        if (driver == null) {
            driver = WebDriverManager.firefoxdriver().create();
        }
    }

    @AfterEach // Changed from @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}