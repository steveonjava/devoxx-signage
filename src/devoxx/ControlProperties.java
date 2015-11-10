/*
 * Devoxx digital signage project
 */
package devoxx;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Level;

/**
 *
 * @author Simon Ritter (@speakjava)
 */
public class ControlProperties {

  public static final int MODE_REAL = 0;
  public static final int MODE_TEST = 1;

  private static final String MODE_REAL_NAME = "REAL";
  private static final String MODE_TEST_NAME = "TEST";

  private Properties properties = new Properties();

  /* Configurable properties and their defaults */
  private Level loggingLevel = Level.INFO;
  private int mode = MODE_REAL;
  private int dataRefreshTime = 30;
  private int screenRefreshTime = 60;
  private String dataURL = "http://cfp.devoxx.be/api/conferences/DV15/";
  private String imageCache = "/home/devoxx/speaker-images";
  private LocalDate startDate;
  private double testScale;
  private int testDay;
  private LocalTime testTime;

  /**
   * Constructor
   *
   * @param fileName The file to load the properties from
   */
  public ControlProperties(String fileName) {
    /* Start by loading the properties */
    try {
      if (fileName != null) {
        System.out.println("Loading parameters from " + fileName);
        properties.load(new FileInputStream(fileName));
      } else /* Load the properties using the file in the jar file */ {
        properties.load(
            Devoxx.class.getResourceAsStream("resources/signage.properties"));
      }
    } catch (IOException ex) {
      System.out.println("ControlProperties: Error reading properties file");
      System.out.println(ex.getMessage());
      System.exit(2);
    }

    /* What level of debug messages to log */
    String value = properties.getProperty("logging-level");

    switch (value) {
      case "ALL":
        loggingLevel = Level.ALL;
        break;
      case "CONFIG":
        loggingLevel = Level.CONFIG;
        break;
      case "FINE":
        loggingLevel = Level.FINE;
        break;
      case "FINER":
        loggingLevel = Level.FINER;
        break;
      case "FINEST":
        loggingLevel = Level.FINEST;
        break;
      case "INFO":
        loggingLevel = Level.INFO;
        break;
      case "OFF":
        loggingLevel = Level.OFF;
        break;
      case "SEVERE":
        loggingLevel = Level.SEVERE;
        break;
      case "WARNING":
        loggingLevel = Level.WARNING;
        break;
    }

    /* How often do we want to refresh the session data (measured in minutes) */
    value = properties.getProperty("data-refresh-time");

    try {
      int i = Integer.parseInt(value);
      dataRefreshTime = i;
    } catch (NumberFormatException nfe) {
      System.out.println(
          "ControlProperties: data-refresh-time is not a number");
    }

    /* How often do we want to refresh the data on the screen */
    value = properties.getProperty("screen-refresh-time");

    try {
      int i = Integer.parseInt(value);
      screenRefreshTime = i;
    } catch (NumberFormatException nfe) {
      System.out.println(
          "ControlProperties: screen-refresh-time is not a number");
    }

    /* Where to get data from */
    value = properties.getProperty("devoxx-data-host");

    if (value != null) {
      dataURL = value;
    }

    value = properties.getProperty("devoxx-start-date");

    /**
     * If there is no start date specified then we can go no further
     */
    if (value == null) {
      System.err.println("ERROR: No start date found in config file");
      System.exit(3);
    }

    startDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
    System.out.println("Start date is: " + startDate);

    /* Where to store the speaker images for caching */
    value = properties.getProperty("image-cache");

    if (value != null) {
      imageCache = value;
    } else {
      imageCache = System.getProperty("user.home") + "/.devoxx-signage";
    }

    /* What mode to run in: real or test */
    value = properties.getProperty("operating-mode");
    String modeName = null;

    if (value != null) {
      switch (value) {
        case MODE_REAL_NAME:
          mode = MODE_REAL;
          modeName = "REAL";
          break;
        case MODE_TEST_NAME:
          mode = MODE_TEST;
          modeName = "TEST";
          break;
        default:
          System.out.println("ControlProperties: Unrecognized mode: " + value);
          break;
      }
    }

    value = properties.getProperty("test-scale");
    testScale = Double.parseDouble(value);

    value = properties.getProperty("test-day");
    testDay = Integer.parseInt(value);

    value = properties.getProperty("test-time");
    testTime = LocalTime.parse(value, DateTimeFormatter.ISO_LOCAL_TIME);

    if (loggingLevel != Level.OFF) {
      System.out.println("\nSYSTEM CONFIGURATION");
      System.out.println("====================");
      System.out.println("logging-level       = " + loggingLevel.toString());
      System.out.println("data-refresh-time   = " + dataRefreshTime);
      System.out.println("screen-refresh-time = " + screenRefreshTime);
      System.out.println("devoxx-host         = " + dataURL);
      System.out.println("image-cache         = " + imageCache);
      System.out.println("mode                = " + modeName);

      if (mode == MODE_TEST) {
        System.out.println("test-scale          = " + testScale);
        System.out.println("test-day            = " + testDay);
        System.out.println("test-time           = " + testTime);
      }

      System.out.println();
    }
  }

  /**
   * Convert a property that represents a boolean into an actual boolean
   *
   * @param key The key for the property
   * @return Whether its value is true or false
   */
  private boolean processBooleanProperty(String key) {
    String value = properties.getProperty(key);

    if (value != null) {
      if (value.compareTo("TRUE") == 0 || value.compareTo("true") == 0) {
        return true;
      }
    }

    return false;
  }

  /**
   * Get what level of messages should be printed
   *
   * @return the logging level
   */
  public Level getLoggingLevel() {
    return loggingLevel;
  }

  /**
   * Get how long we want to wait between data refreshes
   *
   * @return Time (in minutes) between data refreshes
   */
  public int getDataRefreshTime() {
    return dataRefreshTime;
  }

  /**
   * Get how long we want to wait between updates to the screen display
   *
   * @return Time (in seconds) between screen refreshes
   */
  public int getScreenRefreshTime() {
    return screenRefreshTime;
  }

  /**
   * Get the host name of where to retrieve data from
   *
   * @return The host name of where to retrieve data from
   */
  public String getDevoxxHost() {
    return dataURL;
  }

  /**
   * Get the start date of Devoxx
   *
   * @return The date when Devoxx starts
   */
  public LocalDate getStartDate() {
    return startDate;
  }

  /**
   * Get the directory where we want to store the speaker photos for caching
   *
   * @return The directory for photo caching
   */
  public String getImageCache() {
    return imageCache;
  }
  
  /**
   * Are we in test or real mode
   *
   * @return True for test mode
   */
  public boolean isTestMode() {
    return mode == MODE_TEST;
  }

  /**
   * Get the scaling factor to use when testing
   *
   * @return The display scaling factor to use
   */
  public double getTestScale() {
    return testScale;
  }

  /**
   * Get the test day
   *
   * @return The day to yuse for testing
   */
  public int getTestDay() {
    return testDay;
  }

  /**
   * Get the test time
   *
   * @return The time to use for testing
   */
  public LocalTime getTestTime() {
    return testTime;
  }
  
  public void incrementTestTime() {
      testTime = getTestTime().plusHours(1);
      if (getTestTime().isBefore(LocalTime.of(1, 0))) {
          testDay++;
      }
  }
  
  public void decrementTestTime() {
      if (getTestTime().isBefore(LocalTime.of(1, 0))) {
          testDay--;
      }
      testTime = getTestTime().minusHours(1);
  }
}
