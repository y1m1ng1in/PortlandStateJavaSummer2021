package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.InvokeMainTestCase;
import edu.pdx.cs410J.ParserException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for the {@link Project3} main class.
 */
class Project3IT extends InvokeMainTestCase {

  static final String testFile = "unittest.txt";

  void createFileWithText(String content) throws IOException, ParserException {
    File f = new File(testFile);
    f.createNewFile();
    Writer writer = new FileWriter(f);
    writer.write(content);
    writer.flush();
    writer.close();
  }

  String readFile(String f) throws IOException {
    Reader reader = new FileReader(f);
    StringBuilder sb = new StringBuilder();
    int c;
    while((c = reader.read()) != -1) {
      sb.append((char) c);
    }
    reader.close();
    return sb.toString();
  }

  /**
   * Invokes the main method of {@link Project3} with the given arguments.
   */
  private MainMethodResult invokeMain(String... args) {
    return invokeMain( Project3.class, args );
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
    String s = result.getTextWrittenToStandardError();
    assertThat(s, containsString("The following argument(s) are missing:\n"));
    assertThat(s, containsString("* description of the appointment"));
    assertThat(s, containsString("* begin date of the appointment"));
    assertThat(s, containsString("* begin time of the appointment"));
    assertThat(s, containsString("* am/pm marker of begin time of the appointment")); 
    assertThat(s, containsString("* end date of the appointment"));
    assertThat(s, containsString("* end time of the appointment"));
    assertThat(s, containsString("* am/pm marker of end time of the appointment"));
    assertThat(s, not(containsString("* owner")));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with 2 arguments issues an error
   * The error message should indicates the next argument required is missing
   */  
  @Test
  void missingBeginDate() {
    MainMethodResult result = invokeMain("Dave", "A description");
    String s = result.getTextWrittenToStandardError();
    assertThat(s, containsString("The following argument(s) are missing:\n"));
    assertThat(s, not(containsString("* description of the appointment")));
    assertThat(s, containsString("* begin date of the appointment"));
    assertThat(s, containsString("* begin time of the appointment"));
    assertThat(s, containsString("* am/pm marker of begin time of the appointment"));
    assertThat(s, containsString("* end date of the appointment"));
    assertThat(s, containsString("* end time of the appointment"));
    assertThat(s, containsString("* am/pm marker of end time of the appointment"));
    assertThat(s, not(containsString("* owner")));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with 3 arguments issues an error
   * The error message should indicates the next argument required is missing
   */ 
  @Test
  void missingBeginTime() {
    MainMethodResult result = invokeMain("Dave", "A description", "42/52/2020");
    String s = result.getTextWrittenToStandardError();
    assertThat(s, containsString("The following argument(s) are missing:\n"));
    assertThat(s, not(containsString("* description of the appointment")));
    assertThat(s, not(containsString("* begin date of the appointment")));
    assertThat(s, containsString("* begin time of the appointment"));
    assertThat(s, containsString("* am/pm marker of begin time of the appointment"));
    assertThat(s, containsString("* end date of the appointment"));
    assertThat(s, containsString("* end time of the appointment"));
    assertThat(s, containsString("* am/pm marker of end time of the appointment"));
    assertThat(s, not(containsString("* owner")));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void missingBeginMark() {
    MainMethodResult result = invokeMain("Dave", "A description", "42/52/2020", "42:52");
    String s = result.getTextWrittenToStandardError();
    assertThat(s, containsString("The following argument(s) are missing:\n"));
    assertThat(s, not(containsString("* description of the appointment")));
    assertThat(s, not(containsString("* begin date of the appointment")));
    assertThat(s, not(containsString("* begin time of the appointment")));
    assertThat(s, containsString("* am/pm marker of begin time of the appointment"));
    assertThat(s, containsString("* end date of the appointment"));
    assertThat(s, containsString("* end time of the appointment"));
    assertThat(s, containsString("* am/pm marker of end time of the appointment"));
    assertThat(s, not(containsString("* owner")));
    assertThat(result.getExitCode(), equalTo(1));
  }


  /**
   * Tests that invoking the main method with 4 arguments issues an error
   * The error message should indicates the next argument required is missing
   */ 
  @Test
  void missingEndDate() {
    MainMethodResult result = invokeMain("Dave", "A description", "42/52/2020", "42:52", "am");
    String s = result.getTextWrittenToStandardError();
    assertThat(s, containsString("The following argument(s) are missing:\n"));
    assertThat(s, not(containsString("* description of the appointment")));
    assertThat(s, not(containsString("* begin date of the appointment")));
    assertThat(s, not(containsString("* begin time of the appointment")));
    assertThat(s, not(containsString("* am/pm marker of begin time of the appointment")));
    assertThat(s, containsString("* end date of the appointment"));
    assertThat(s, containsString("* end time of the appointment"));
    assertThat(s, containsString("* am/pm marker of end time of the appointment"));
    assertThat(s, not(containsString("* owner")));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with 5 arguments issues an error
   * The error message should indicates the next argument required is missing
   */ 
  @Test
  void missingEndTime() {
    MainMethodResult result = invokeMain("Dave", "A description", "42/52/2020", "42:52", "am", "42/52/2020");
    String s = result.getTextWrittenToStandardError();
    assertThat(s, containsString("The following argument(s) are missing:\n"));
    assertThat(s, not(containsString("* description of the appointment")));
    assertThat(s, not(containsString("* begin date of the appointment")));
    assertThat(s, not(containsString("* begin time of the appointment")));
    assertThat(s, not(containsString("* am/pm marker of begin time of the appointment")));
    assertThat(s, not(containsString("* end date of the appointment")));
    assertThat(s, containsString("* end time of the appointment"));
    assertThat(s, containsString("* am/pm marker of end time of the appointment"));
    assertThat(s, not(containsString("* owner")));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void missingEndMark() {
    MainMethodResult result = invokeMain("Dave", "A description", "42/52/2020", "42:52", "am", "42/52/2020", "1:1");
    String s = result.getTextWrittenToStandardError();
    assertThat(s, containsString("The following argument(s) are missing:\n"));
    assertThat(s, not(containsString("* description of the appointment")));
    assertThat(s, not(containsString("* begin date of the appointment")));
    assertThat(s, not(containsString("* begin time of the appointment")));
    assertThat(s, not(containsString("* am/pm marker of begin time of the appointment")));
    assertThat(s, not(containsString("* end date of the appointment")));
    assertThat(s, not(containsString("* end time of the appointment")));
    assertThat(s, containsString("* am/pm marker of end time of the appointment"));
    assertThat(s, not(containsString("* owner")));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with more arguments than needed
   * When there are 9 arguments passed in, they are treated as 1 option followed by 8 arguments
   */
  @Test
  void moreArgumentsThanNeeded() {
    MainMethodResult result = invokeMain("Dave", "A description", "42/52/2020", "42:52", "am", "42/52/2020", "42:52", "am", "extra1");
    String s = result.getTextWrittenToStandardError();
    assertThat(s, containsString("The following option(s) cannot be recognized:\n  * Dave\n"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with print option and valid arguments and 
   * correct argument number.
   * The program should correctly print the appointment.
   */
  @Test
  void printAppointmentWithValidArguments() {
    MainMethodResult result = invokeMain("-print", "Dave", "A description", "2/12/2020", "12:52", "am", "4/5/2020", "2:52", "pm");
    assertThat(result.getTextWrittenToStandardError(), emptyString());
    String s = "A description from 2/12/20, 12:52 AM until 4/5/20, 2:52 PM";
    assertThat(result.getTextWrittenToStandardOut(), containsString(s));
    assertThat(result.getExitCode(), equalTo(0));
  }

  /**
   * Tests that invoking the main method with month out of range issues an error
   */
  @Test
  void invalidRangeOfBeginDate() {
    MainMethodResult result = invokeMain("-print", "Dave", "A description", "22/12/2020", "12:52", "am", "4/5/2020", "2:52", "pm");
    assertThat(result.getTextWrittenToStandardError(), containsString("Unparseable date"));
    assertThat(result.getTextWrittenToStandardOut(), emptyString());
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with hour out of range issues an error
   */
  @Test
  void invalidRangeOfBeginTime() {
    MainMethodResult result = invokeMain("-print", "Dave", "A description", "12/12/2020", "120:52", "am", "4/5/2020", "2:52", "pm");
    assertThat(result.getTextWrittenToStandardError(), containsString("Unparseable date"));
    assertThat(result.getTextWrittenToStandardOut(), emptyString());
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with date delimited by wrong delimiter issues an error
   */
  @Test
  void invalidDelimiterFormatBeginDate() {
    MainMethodResult result = invokeMain("-print", "Dave", "A description", "12-12-2020", "120:52", "am", "4/5/2020", "2:52", "pm");
    assertThat(result.getTextWrittenToStandardError(), containsString("Unparseable date"));
    assertThat(result.getTextWrittenToStandardOut(), emptyString());
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with date in wrong format issues an error
   */
  @Test
  void invalidStringFormatBeginDate() {
    MainMethodResult result = invokeMain("-print", "Dave", "A description", "begindate", "120:52", "am", "4/5/2020", "2:52", "pm");
    assertThat(result.getTextWrittenToStandardError(), containsString("Unparseable date"));
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
    MainMethodResult result = invokeMain("-print", "-print", "Dave", "A description", "begindate", "12:52", "am", "4/5/2020", "2:52", "pm");
    assertThat(result.getTextWrittenToStandardError(), containsString("duplicated"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with only one argument -README
   */
  @Test
  void onlyPassInReadme() {
    MainMethodResult result = invokeMain("-README");
    assertThat(result.getTextWrittenToStandardError(), containsString("usage"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with only one argument which is not 
   * a valid option. In this case, the program should treat it as "owner" argument.
   */
  @Test
  void onlyPassInOneInvalidOption() {
    MainMethodResult result = invokeMain("-READMEE");
    String s = result.getTextWrittenToStandardError();
    assertThat(s, containsString("The following argument(s) are missing:\n"));
    assertThat(s, containsString("* description of the appointment"));
    assertThat(s, containsString("* begin date of the appointment"));
    assertThat(s, containsString("* begin time of the appointment"));
    assertThat(s, containsString("* am/pm marker of begin time of the appointment")); 
    assertThat(s, containsString("* end date of the appointment"));
    assertThat(s, containsString("* end time of the appointment"));
    assertThat(s, containsString("* am/pm marker of end time of the appointment"));
    assertThat(s, not(containsString("* owner")));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with only one argument which is 
   * a valid option. In this case, the program should treat it as an option, thus
   * missing argument message should print.
   */
  @Test
  void onlyPassInOneValidOption() {
    MainMethodResult result = invokeMain("-print");
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing command line arguments"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with invalid option and correct argument number, in this case 
   * even arugments are invalid, should output message about invalid option. 
   */
  @Test
  void InvalidOneOptions() {
    MainMethodResult result = invokeMain("-READMEE", "Dave", "A description", "42/52/2020", "42:52", "am", "42/52/2020", "extra1", "am");
    String s = result.getTextWrittenToStandardError();
    assertThat(s, containsString("The following option(s) cannot be recognized:\n  * -READMEE\n"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with invalid option and correct argument number, in this case 
   * even arugments are invalid, should output message about invalid option. 
   */
  @Test
  void InvalidTwoOptions() {
    MainMethodResult result = invokeMain(
        "-READMEE", "-READMEEE", "Dave", "A description", "42/52/2020", "42:52", "am", "42/52/2020", "extra1", "am");
    String s = result.getTextWrittenToStandardError();
    assertThat(s, containsString("The following option(s) cannot be recognized:\n  * -READMEE\n  * -READMEEE\n"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with empty description.
   */
  @Test
  void EmptyDescription() {
    MainMethodResult result = invokeMain("Dave", "", "12/22/2020", "2:52", "AM", "12/5/2020", "3:50", "PM");
    assertThat(result.getTextWrittenToStandardError(), containsString("description should not be empty"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with description of a sequence of spaces, which should be treated
   * as empty description.  
   */
  @Test
  void EmptyDescriptionWithMultipleSpacesIsAlsoEmpty() {
    MainMethodResult result = invokeMain("Dave", "     ", "12/22/2020", "2:52", "AM", "12/5/2020", "3:50", "PM");
    assertThat(result.getTextWrittenToStandardError(), containsString("description should not be empty"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with empty owner.
   */
  @Test
  void EmptyOwner() {
    MainMethodResult result = invokeMain("", "desc", "12/22/2020", "2:52", "AM", "12/5/2020", "3:50", "PM");
    assertThat(result.getTextWrittenToStandardError(), containsString("owner should not be empty"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with owner of a sequence of spaces, which should be treated
   * as empty owner.  
   */
  @Test
  void EmptyOwnerWithMultipleSpacesIsAlsoEmpty() {
    MainMethodResult result = invokeMain("       ", "desc", "12/22/2020", "2:52", "AM", "12/5/2020", "3:50", "PM");
    assertThat(result.getTextWrittenToStandardError(), containsString("owner should not be empty"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void ValidArgsWithFile() throws IOException, ParserException {
    createFileWithText("yml&02/02/2020 12:52 am#4/5/2020 2:52 pm#A description1&");
    MainMethodResult result = invokeMain(
        "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "am", "4/5/2020", "2:52", "pm");
    String s = readFile(testFile);
    String expected = 
        "yml&02/02/2020 12:52 am#4/5/2020 2:52 pm#A description1&2/12/2020 12:52 am#4/5/2020 2:52 pm#A description&";
    assertThat(s, equalTo(expected));
    assertThat(result.getExitCode(), equalTo(0));
  }

  @Test
  void ValidArgWithNonexistFile() throws IOException {
    MainMethodResult result = invokeMain(
      "-textFile", "nonexists.txt", "yml", "A description", "2/12/2020", "12:52", "pm", "4/5/2020", "2:52", "AM");
    String s = readFile("nonexists.txt");
    String expected = "yml&2/12/2020 12:52 pm#4/5/2020 2:52 AM#A description&";
    assertThat(s, containsString(expected));
    assertThat(result.getExitCode(), equalTo(0));
  }

  @Test
  void missingFileArgWhenTextFileEnabled() throws IOException {
    MainMethodResult result = invokeMain(
      "-print", "-textFile");
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing argument of option -textFile"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void readmeWithTextFile() throws IOException {
    MainMethodResult result = invokeMain(
      "-README", "-print", "-textFile");
    assertThat(result.getTextWrittenToStandardError(), containsString("usage"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void fileNameAlwaysAfterTextFile() throws IOException {
    MainMethodResult result = invokeMain(
      "-print", "-textFile", "-README");
    assertThat(result.getTextWrittenToStandardError(), 
               containsString("Missing command line arguments"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void shouldPrintReadmeAfterReadmeAsArgOfTextFile() throws IOException {
    MainMethodResult result = invokeMain(
      "-print", "-textFile", "-README", "-README");
    assertThat(result.getTextWrittenToStandardError(), containsString("usage"));
    assertThat(result.getExitCode(), equalTo(1));
  }


  @Test
  void readmeShouldBePrintedEvenDuplicated() {
    MainMethodResult result = invokeMain(
      "-print","-README", "-README", "-textFile", testFile);
    assertThat(result.getTextWrittenToStandardError(), containsString("usage"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void otherOptionMustNotDuplicated() {
    MainMethodResult result = invokeMain(
      "-print","-print", "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "4/5/2020", "2:52");
    assertThat(result.getTextWrittenToStandardError(), containsString("duplicated -print in options"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void fileMissingOwnername() throws IOException, ParserException {
    createFileWithText("&02/02/2020 12:52 am#4/5/2020 2:52 pm#A description1&");
    MainMethodResult result = invokeMain(
        "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "PM", "4/5/2020", "2:52", "AM");
    String expected = "Field owner should not be empty";
    assertThat(result.getTextWrittenToStandardError(), containsString(expected));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void fileWithOnlySpacesOwnername() throws IOException, ParserException {
    createFileWithText("    &02/02/2020 12:52 pm#4/5/2020 2:52 pm#A description1&");
    MainMethodResult result = invokeMain(
        "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "am", "4/5/2020", "2:52", "am");
    String expected = "Field owner should not be empty";
    assertThat(result.getTextWrittenToStandardError(), containsString(expected));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void fileWithFewArgumentsForAppointment() throws IOException, ParserException {
    createFileWithText("yml&02/02/2020 12:52 am#4/5/2020 2:52 am#A description1&02/02/2020 12:52 am 4/5/2020 2:52 am#A description1&02/02/2020#12:52#4/5/2020#2:52#A description1&");
    MainMethodResult result = invokeMain(
        "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "am", "4/5/2020", "2:52", "pm");
    String expected = "Not enough fields to build appointment from file expect 3, but got 2";
    assertThat(result.getTextWrittenToStandardError(), containsString(expected));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void fileWithMoreArgumentsForAppointment() throws IOException, ParserException {
    createFileWithText("yml&02/02/2020#12:52#4/5/2020#2:52#A description112/12/2022#12:52#4/5/2023#2:52#A description2&");
    MainMethodResult result = invokeMain(
        "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "am", "4/5/2020", "2:52", "am");
    String expected = "An extraneous field encountered to build appointment from file";
    assertThat(result.getTextWrittenToStandardError(), containsString(expected));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void prettyPrintToStandardOut() throws IOException, ParserException {
    createFileWithText("yml&02/02/2020 12:52 pm#2/2/2020 2:52 pm#longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglong description&");
    MainMethodResult result = invokeMain(
      "-textFile", testFile, "-pretty", "-", "yml", "A description", "2/12/2020", "12:52", "pm", "2/12/2020", "2:52", "pm");
      String exp = 
        "Owner        |  yml\n" +
        "----------------------------------------\n" +
        "Begin at     |  2/2/20, 12:52 PM\n" + 
        "End at       |  2/2/20, 2:52 PM\n" +
        "Description  |  longlonglonglonglonglong\n" +
        "             |  longlonglonglonglonglong\n" +
        "             |  longlonglonglonglonglong\n" +
        "             |   description\n"+
        "Duration     |  120 minutes\n" + 
        "----------------------------------------\n" +
        "Begin at     |  2/12/20, 12:52 PM\n" + 
        "End at       |  2/12/20, 2:52 PM\n" +
        "Description  |  A description\n" +
        "Duration     |  120 minutes\n" + 
        "----------------------------------------\n";
    assertThat(result.getTextWrittenToStandardOut(), equalTo(exp));
    assertThat(result.getExitCode(), equalTo(0));
  }

  @Test
  void prettyPrintToFile() throws IOException, ParserException {
    createFileWithText("yml&02/02/2020 12:52 pm#2/2/2020 2:52 pm#longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglong description&");
    MainMethodResult result = invokeMain(
      "-textFile", testFile, "-pretty", "nonexists1.txt", "yml", "A description", "2/12/2020", "12:52", "pm", "2/12/2020", "2:52", "pm");
      String exp = 
        "Owner        |  yml\n" +
        "----------------------------------------\n" +
        "Begin at     |  2/2/20, 12:52 PM\n" + 
        "End at       |  2/2/20, 2:52 PM\n" +
        "Description  |  longlonglonglonglonglong\n" +
        "             |  longlonglonglonglonglong\n" +
        "             |  longlonglonglonglonglong\n" +
        "             |   description\n"+
        "Duration     |  120 minutes\n" + 
        "----------------------------------------\n" +
        "Begin at     |  2/12/20, 12:52 PM\n" + 
        "End at       |  2/12/20, 2:52 PM\n" +
        "Description  |  A description\n" +
        "Duration     |  120 minutes\n" + 
        "----------------------------------------\n";
    assertThat(readFile("nonexists1.txt"), equalTo(exp));
    assertThat(result.getExitCode(), equalTo(0));
  }

  @Test
  void fileConflictTest() throws IOException, ParserException {
    createFileWithText("yml&02/02/2020 12:52 pm#2/2/2020 2:52 pm#longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglong description&");
    MainMethodResult result = invokeMain(
      "-textFile", testFile, "-pretty", testFile, "yml", "A description", "2/12/2020", "12:52", "pm", "2/12/2020", "2:52", "pm");
    assertThat(result.getTextWrittenToStandardError(), containsString("Cannot dump appointment book and pretty print to same file"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void fileWithUnparseableArgumentsForAppointmentCase1() throws IOException, ParserException {
    createFileWithText("yml&02/02/2020 12:52 pm#4/5/2020 2:52 pm#A description&12/12/2022 12:52pm#4/5/2023 2:52 am#A description2&");
    MainMethodResult result = invokeMain(
        "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "am", "4/5/2020", "2:52", "am");
    String expected = "Unparseable date: \"12/12/2022 12:52pm\"";
    assertThat(result.getTextWrittenToStandardError(), containsString(expected));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void fileWithUnparseableArgumentsForAppointmentCase2() throws IOException, ParserException {
    createFileWithText("yml&02/02/2020 12:52 pm#4/5/2020 2:52 pm#A description&12/12/2022 12:52 pm#040523 2:52 am#A description2&");
    MainMethodResult result = invokeMain(
        "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "am", "4/5/2020", "2:52", "am");
    String expected = "Unparseable date: \"040523 2:52 am\"";
    assertThat(result.getTextWrittenToStandardError(), containsString(expected));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void fileWithUnparseableArgumentsForAppointmentCase3() throws IOException, ParserException {
    createFileWithText("yml&02/02/2020 12:52 pm#4/5/2020 2:52 pm#&12/12/2022 12:52 pm#04/05/2023 2:52 am#A description2&");
    MainMethodResult result = invokeMain(
        "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "am", "4/5/2020", "2:52", "am");
    String expected = "Field description should not be empty";
    assertThat(result.getTextWrittenToStandardError(), containsString(expected));
    assertThat(result.getExitCode(), equalTo(1));
  }
  
  @Test
  void fileWithUnparseableArgumentsForAppointmentCase4() throws IOException, ParserException {
    createFileWithText("yml&02/02/2020 12:52 pm#4/5/2020 2:52 pm#   &12/12/2022 12:52 pm#04/05/2023 2:52 am#A description2&");
    MainMethodResult result = invokeMain(
        "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "am", "4/5/2020", "2:52", "am");
    String expected = "Field description should not be empty";
    assertThat(result.getTextWrittenToStandardError(), containsString(expected));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void fileWithUnparseableArgumentsForAppointmentCase5() throws IOException, ParserException {
    createFileWithText("yml&begintime#4/5/2020 2:52 pm#desc&12/12/2022 12:52 pm#04/05/2023 2:52 am#A description2&");
    MainMethodResult result = invokeMain(
        "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "am", "4/5/2020", "2:52", "am");
    String expected = "Unparseable date: \"begintime\"";
    assertThat(result.getTextWrittenToStandardError(), containsString(expected));
    assertThat(result.getExitCode(), equalTo(1));
  }
}