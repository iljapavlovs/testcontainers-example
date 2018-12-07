package org.selenide.examples;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.WebDriverRunner;
import java.io.File;
import java.util.Optional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.VncRecordingContainer;
import org.testcontainers.lifecycle.TestDescription;

public class GoogleTestWithVanillaInegration {

/*
so then the only way to use testcontainers with videos enabled in ‘vanilla’ mode is with additional VncRecordingContainer container?

Two ways:) Either:
1) call afterTest method
2) use VncRecordingContainerqq


In JUnit 5 there is TestInfo injection which simplifies test meta data providing to test methods. For example:

@Test
@DisplayName("This is my test")
@Tag("It is my tag")
void test1(TestInfo testInfo) {
    assertEquals("This is my test", testInfo.getDisplayName());
    assertTrue(testInfo.getTags().contains("It is my tag"));
}
 */
  public static BrowserWebDriverContainer browser =
      new BrowserWebDriverContainer()
          .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL, new File("build"))
          .withDesiredCapabilities(DesiredCapabilities.firefox());


//  VncRecordingContainer vncRecordingContainer = new VncRecordingContainer(browser)
//                    .withVncPassword(DEFAULT_PASSWORD)
//                    .withVncPort(VNC_PORT);

  @Rule
  public TestWatcher watchman = new TestWatcher() {
    @Override
    public void starting(Description d) {
      System.out.println(String.format("'%s' running...", d.getClassName() + "_" + d.getMethodName()));
    }

    @Override
    public void failed(Throwable e, Description d) {
      System.out.println(String.format("'%s' failed...", d.getClassName() + "_" + d.getMethodName()));
    }

    @Override
    public void finished(Description d) {

      browser.afterTest(toDescription(d), Optional.empty());

      System.out.println(String.format("'%s' finished...", d.getClassName() + "_" + d.getMethodName()));
    }
  };



  @BeforeClass
  public static void setupClass(){
    browser.start();
  }


  @AfterClass
  public static void afterClass(){
    browser.stop();
  }



  @Before
  public void setUp() {
    RemoteWebDriver driver = browser.getWebDriver();
    System.out.println(browser.getVncAddress());
    WebDriverRunner.setWebDriver(driver);
  }

  @After
  public void tearDown() {

    WebDriverRunner.closeWebDriver();

  }

  @Test
  public void search() {
    open("https://www.google.com/en");
    $(By.name("q")).val("codeborne").pressEnter();
    $$("#ires .g").shouldHave(sizeGreaterThan(5));
  }


  private TestDescription toDescription(final Description description) {
    return new TestDescription() {
      public String getTestId() {
        return description.getDisplayName();
      }

      public String getFilesystemFriendlyName() {
        return description.getClassName() + "-" + description.getMethodName();
      }
    };
  }
}
