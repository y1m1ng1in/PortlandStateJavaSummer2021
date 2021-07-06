package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for the {@link Project1} main class.
 */
class Project1IT extends InvokeMainTestCase {

  /**
   * Invokes the main method of {@link Project1} with the given arguments.
   */
  private MainMethodResult invokeMain(String... args) {
    return invokeMain( Project1.class, args );
  }

  /**
   * Tests that invoking the main method with no arguments issues an error
   */
  @Test
  void testNoCommandLineArguments() {
    MainMethodResult result = invokeMain();
    assertThat(result.getExitCode(), equalTo(1));
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing command line arguments"));
  }

  /**
   * Tests that invoking the main method with 1 argument issues an error
   * The error message should indicates the next argument required is missing
   */
  @Test
  void missingDescription() {
    MainMethodResult result = invokeMain("Dave");
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing description of the appointment"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with 2 arguments issues an error
   * The error message should indicates the next argument required is missing
   */  
  @Test
  void missingBeginDate() {
    MainMethodResult result = invokeMain("Dave", "A description");
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing begin date of the appointment"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with 3 arguments issues an error
   * The error message should indicates the next argument required is missing
   */ 
  @Test
  void missingBeginTime() {
    MainMethodResult result = invokeMain("Dave", "A description", "42/52/2020");
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing begin time of the appointment"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with 4 arguments issues an error
   * The error message should indicates the next argument required is missing
   */ 
  @Test
  void missingEndDate() {
    MainMethodResult result = invokeMain("Dave", "A description", "42/52/2020", "42:52");
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing end date of the appointment"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with 5 arguments issues an error
   * The error message should indicates the next argument required is missing
   */ 
  @Test
  void missingEndTime() {
    MainMethodResult result = invokeMain("Dave", "A description", "42/52/2020", "42:52", "42/52/2020");
    System.out.println(result);
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing end time of the appointment"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with more arguments than needed
   * When there are 7 arguments passed in, they are treated as 1 option followed by 6 arguments
   */
  @Test
  void moreArgumentsThanNeeded() {
    MainMethodResult result = invokeMain("Dave", "A description", "42/52/2020", "42:52", "42/52/2020", "extra1", "extra2");
    assertThat(result.getTextWrittenToStandardError(), containsString("Dave is not an available switch"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with print option and valid arguments and 
   * correct argument number.
   * The program should correctly print the appointment.
   */
  @Test
  void printAppointmentWithValidArguments() {
    MainMethodResult result = invokeMain("-print", "Dave", "A description", "2/12/2020", "12:52", "4/5/2020", "2:52");
    assertThat(result.getTextWrittenToStandardError(), emptyString());
    String s = "A description from 2/12/2020 12:52 until 4/5/2020 2:52";
    assertThat(result.getTextWrittenToStandardOut(), containsString(s));
    assertThat(result.getExitCode(), equalTo(0));
  }

  /**
   * Tests that invoking the main method with month out of range issues an error
   */
  @Test
  void invalidRangeOfBeginDate() {
    MainMethodResult result = invokeMain("-print", "Dave", "A description", "32/12/2020", "12:52", "4/5/2020", "2:52");
    assertThat(result.getTextWrittenToStandardError(), containsString("not a valid month"));
    assertThat(result.getTextWrittenToStandardOut(), emptyString());
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with hour out of range issues an error
   */
  @Test
  void invalidRangeOfBeginTime() {
    MainMethodResult result = invokeMain("-print", "Dave", "A description", "12/12/2020", "120:52", "4/5/2020", "2:52");
    assertThat(result.getTextWrittenToStandardError(), containsString("format does not meet requirement"));
    assertThat(result.getTextWrittenToStandardOut(), emptyString());
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with date delimited by wrong delimiter issues an error
   */
  @Test
  void invalidDelimiterFormatBeginDate() {
    MainMethodResult result = invokeMain("-print", "Dave", "A description", "12-12-2020", "120:52", "4/5/2020", "2:52");
    assertThat(result.getTextWrittenToStandardError(), containsString("format does not meet requirement"));
    assertThat(result.getTextWrittenToStandardOut(), emptyString());
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with date in wrong format issues an error
   */
  @Test
  void invalidStringFormatBeginDate() {
    MainMethodResult result = invokeMain("-print", "Dave", "A description", "begindate", "120:52", "4/5/2020", "2:52");
    assertThat(result.getTextWrittenToStandardError(), containsString("format does not meet requirement"));
    assertThat(result.getTextWrittenToStandardOut(), emptyString());
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with readme option prints the readme info
   * and exit without doing anything.
   */
  @Test
  void onlyPrintReadmeWithOneSwitch() {
    MainMethodResult result = invokeMain("-README", "Dave", "A description", "42/52/2020", "42:52", "42/52/2020", "extra1", "extra2");
    assertThat(result.getTextWrittenToStandardError(), containsString("usage"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with readme option prints the readme info
   * and exit without doing anything.
   */
  @Test
  void onlyPrintReadmeWithTwoSwitches() {
    MainMethodResult result = invokeMain("-print", "-README", "Dave", "A description", "42/52/2020", "42:52", "42/52/2020", "extra1", "extra2");
    assertThat(result.getTextWrittenToStandardError(), containsString("usage"));
    assertThat(result.getExitCode(), equalTo(1));
  }
  
  /**
   * Tests that invoking the main method with readme option prints the readme info
   * and exit without doing anything.
   */
  @Test
  void onlyPrintReadmeWithTwoSwitchesOrderDoesNotMatter() {
    MainMethodResult result = invokeMain("-README", "-print", "Dave", "A description", "42/52/2020", "42:52", "42/52/2020", "extra1", "extra2");
    assertThat(result.getTextWrittenToStandardError(), containsString("usage"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with duplicated options 
   */
  @Test
  void duplicatedOptionsPassedIn() {
    MainMethodResult result = invokeMain("-print", "-print", "Dave", "A description", "42/52/2020", "42:52", "42/52/2020", "extra1");
    assertThat(result.getTextWrittenToStandardError(), containsString("duplicated"));
    assertThat(result.getExitCode(), equalTo(1));
  }
}