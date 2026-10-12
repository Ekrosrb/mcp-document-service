package com.ekros.hl.doc.it.support;

import com.ekros.hl.doc.Application;
import com.ekros.hl.doc.it.testutil.ItTestData;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Runs the service under test with the IT profile for the whole test run.
 */
public class ApplicationHooks {

  private static ConfigurableApplicationContext application;

  @BeforeAll
  public static void startApplication() {
    application = new SpringApplicationBuilder(Application.class)
        .profiles(ItTestData.IT_PROFILE)
        .run();
  }

  @AfterAll
  public static void stopApplication() {
    if (application != null) {
      application.close();
    }
  }

  public static String property(String name) {
    return application.getEnvironment().getRequiredProperty(name);
  }
}
