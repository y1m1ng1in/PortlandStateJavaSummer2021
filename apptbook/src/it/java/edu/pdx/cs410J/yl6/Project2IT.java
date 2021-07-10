package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.InvokeMainTestCase;
import edu.pdx.cs410J.ParserException;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.emptyString;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for the {@link Project2} main class.
 */
class Project2IT extends InvokeMainTestCase {

  static final String testFile = "unittest.txt";

  void createFileWithText(String content) throws IOException, ParserException {
    File f = new File(testFile);
    f.createNewFile();
    Writer writer = new FileWriter(testFile);
    writer.write(content);
    writer.flush();
    writer.close();
  }

  String readFile() throws IOException {
    Reader reader = new FileReader(testFile);
    StringBuilder sb = new StringBuilder();
    int c;
    while((c = reader.read()) != -1) {
      sb.append((char) c);
    }
    reader.close();
    return sb.toString();
  }

  /**
   * Invokes the main method of {@link Project2} with the given arguments.
   */
  private MainMethodResult invokeMain(String... args) {
    return invokeMain( Project2.class, args );
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
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing description of the appointment"));
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
    MainMethodResult result = invokeMain("-READMEE", "Dave", "A description", "42/52/2020", "42:52", "42/52/2020", "extra1");
    assertThat(result.getTextWrittenToStandardError(), containsString("is not an available switch"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with invalid option and correct argument number, in this case 
   * even arugments are invalid, should output message about invalid option. 
   */
  @Test
  void InvalidTwoOptions() {
    MainMethodResult result = invokeMain("-READMEE", "-READMEEE", "Dave", "A description", "42/52/2020", "42:52", "42/52/2020", "extra1");
    assertThat(result.getTextWrittenToStandardError(), containsString("is not an available switch"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with empty description.
   */
  @Test
  void EmptyDescription() {
    MainMethodResult result = invokeMain("Dave", "", "12/22/2020", "2:52", "12/5/2020", "3:50");
    assertThat(result.getTextWrittenToStandardError(), containsString("description should not be empty"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with description of a sequence of spaces, which should be treated
   * as empty description.  
   */
  @Test
  void EmptyDescriptionWithMultipleSpacesIsAlsoEmpty() {
    MainMethodResult result = invokeMain("Dave", "     ", "12/22/2020", "2:52", "12/5/2020", "3:50");
    assertThat(result.getTextWrittenToStandardError(), containsString("description should not be empty"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with empty owner.
   */
  @Test
  void EmptyOwner() {
    MainMethodResult result = invokeMain("", "desc", "12/22/2020", "2:52", "12/5/2020", "3:50");
    assertThat(result.getTextWrittenToStandardError(), containsString("owner should not be empty"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  /**
   * Tests that invoking the main method with owner of a sequence of spaces, which should be treated
   * as empty owner.  
   */
  @Test
  void EmptyOwnerWithMultipleSpacesIsAlsoEmpty() {
    MainMethodResult result = invokeMain("       ", "desc", "12/22/2020", "2:52", "12/5/2020", "3:50");
    assertThat(result.getTextWrittenToStandardError(), containsString("owner should not be empty"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void ValidArgsWithFile() throws IOException, ParserException {
    createFileWithText("yml&02/02/2020#12:52#4/5/2020#2:52#A description1&");
    MainMethodResult result = invokeMain(
        "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "4/5/2020", "2:52");
    String s = readFile();
    String expected = 
        "yml&02/02/2020#12:52#4/5/2020#2:52#A description1&2/12/2020#12:52#4/5/2020#2:52#A description&";
    assertThat(s, equalTo(expected));
    assertThat(result.getExitCode(), equalTo(0));
  }

  @Test
  void ValidArgWithNonexistFile() throws IOException {
    File f = new File(testFile);
    f.delete();
    MainMethodResult result = invokeMain(
      "-textFile", testFile, "yml", "A description", "2/12/2020", "12:52", "4/5/2020", "2:52");
    String s = readFile();
    String expected = "yml&2/12/2020#12:52#4/5/2020#2:52#A description&";
    assertThat(s, equalTo(expected));
    assertThat(result.getExitCode(), equalTo(0));
  }

  @Test
  void missingFileArgWhenTextFileEnabled() throws IOException {
    File f = new File(testFile);
    f.delete();
    MainMethodResult result = invokeMain(
      "-print", "-textFile");
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing argument of option -textFile"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void readmeWithTextFile() throws IOException {
    File f = new File(testFile);
    f.delete();
    MainMethodResult result = invokeMain(
      "-README", "-print", "-textFile");
    assertThat(result.getTextWrittenToStandardError(), containsString("usage"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void fileNameAlwaysAfterTextFile() throws IOException {
    File f = new File(testFile);
    f.delete();
    MainMethodResult result = invokeMain(
      "-print", "-textFile", "-README");
    assertThat(result.getTextWrittenToStandardError(), 
               containsString("Missing command line arguments"));
    assertThat(result.getExitCode(), equalTo(1));
  }

  @Test
  void shouldPrintReadmeAfterReadmeAsArgOfTextFile() throws IOException {
    File f = new File(testFile);
    f.delete();
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
}