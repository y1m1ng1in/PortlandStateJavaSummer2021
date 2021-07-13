package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;
import edu.pdx.cs410J.ParserException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

/**
 * Unit tests for the {@link TextParser} class.
 */
public class TextParserTest {

  static final String testFile = "unittest.txt";

  void createFileWithText(String content) throws IOException, ParserException {
      File f = new File(testFile);
      f.createNewFile();
      Writer writer = new FileWriter(testFile);
      writer.write(content);
      writer.flush();
      writer.close();
  }

  AbstractValidator[] createValidators() {
    DateStringValidator dateValidator = new DateStringValidator();
    TimeStringValidator timeValidator = new TimeStringValidator();
    NonemptyStringValidator descriptionValidator = new NonemptyStringValidator("description");
    AbstractValidator[] validators = {
      dateValidator, timeValidator, dateValidator, timeValidator, descriptionValidator
    };
    return validators;
  }

  /**
   * Tests that correct begin date and time is returned as a string.
   */
  @Test
  void wellFormattedWithOneAppt() throws IOException, ParserException {
    createFileWithText("yml&2/2/2020#2:22#2/3/2020#3:33#descrp&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "yml", AppointmentBook.class, Appointment.class, 
                     new NonemptyStringValidator("owner"), createValidators(), 5);
    AppointmentBook<Appointment> book = textParser.parse();  
    Appointment inBook = book.getAppointments().get(0);
    
    String s = inBook.getBeginTimeString();
    assertThat(s, equalTo("2/2/2020 2:22"));
    s = inBook.getEndTimeString();
    assertThat(s, equalTo("2/3/2020 3:33"));
    s = inBook.getDescription();
    assertThat(s, equalTo("descrp"));
  }

  @Test
  void wellFormattedWithTwoAppts() throws IOException, ParserException {
    createFileWithText(
        "yml&2/2/2020#2:22#2/3/2020#3:33#descrp&11/14/2021#12:30#11/15/2021#14:45#desc  des ddd &");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "yml", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    AppointmentBook<Appointment> book = textParser.parse();  
    Appointment inBook1 = book.getAppointments().get(0);
    Appointment inBook2 = book.getAppointments().get(1);

    String s = inBook1.getBeginTimeString();
    assertThat(s, equalTo("2/2/2020 2:22"));
    s = inBook1.getEndTimeString();
    assertThat(s, equalTo("2/3/2020 3:33"));
    s = inBook1.getDescription();
    assertThat(s, equalTo("descrp"));

    s = inBook2.getBeginTimeString();
    assertThat(s, equalTo("11/14/2021 12:30"));
    s = inBook2.getEndTimeString();
    assertThat(s, equalTo("11/15/2021 14:45"));
    s = inBook2.getDescription();
    assertThat(s, equalTo("desc  des ddd "));
  }

  @Test
  void wellFormattedWithEscapedCharWithOneAppt() throws IOException, ParserException {
    createFileWithText(
        "\\#\\#\\#\\&\\&\\&\\#\\#\\#&2/2/2020#2:22#2/3/2020#3:33#\\&\\&des\\&c\\&\\&\\&\\&&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "###&&&###", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    AppointmentBook<Appointment> book = textParser.parse();  
    Appointment inBook1 = book.getAppointments().get(0);

    String s = inBook1.getBeginTimeString();
    assertThat(s, equalTo("2/2/2020 2:22"));
    s = inBook1.getEndTimeString();
    assertThat(s, equalTo("2/3/2020 3:33"));
    s = inBook1.getDescription();
    assertThat(s, equalTo("&&des&c&&&&"));
  }

  @Test
  void missingAmpersandInTheEnd() throws IOException, ParserException {
    createFileWithText(
        "\\#\\#\\#\\\\\\\\\\#\\#\\#&2/2/2020#2:22#2/3/2020#3:33#\\&\\&des\\&c\\&\\&\\&\\");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "###\\\\###", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("End of file reached before the last appointment been parsed completely"));
  }

  @Test
  void missingArgumentToBuildAppt() throws IOException, ParserException {
    createFileWithText(
        "\\#\\#\\#\\\\\\\\\\#\\#\\#&2/2/2020 2:22 2/3/2020 3:33#\\&\\&des\\&c\\&\\&\\&\\&&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "###\\\\###", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("Not enough fields to build appointment from file expect 5, but got 2"));
  }

  @Test
  void missingOwner() throws IOException, ParserException {
    createFileWithText(
        "2/2/2020 2:22 2/3/2020 3:33#\\&\\&des\\&c\\&\\&\\&\\&&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "###\\\\###", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("Prohibited character # occurs when parsing owner name."));
  }


  @Test
  void apptMissingEverything() throws IOException, ParserException {
    createFileWithText(
        "owner&&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "owner", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("Not enough fields to build appointment from file expect 5, but got 1"));
  }

  @Test
  void onlyOwner() throws IOException, ParserException {
    createFileWithText(
        "owner&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "owner", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("End of file reached before the last appointment been parsed completely"));
  }

  @Test
  void ownerMismatch() throws IOException, ParserException {
    createFileWithText(
        "owner&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "owner1", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        containsString("Owner parsed from file is mismatched with argument"));
  }

  @Test
  void fileNotExist() throws IOException, ParserException {
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser("notexist.txt", "owner1", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    AppointmentBook book = textParser.parse();
    assertThat(book.getAppointments().size(), equalTo(0));
    File f = new File("notexist.txt");
    f.delete();
  }

  @Test
  void validatorViolationCase1() throws IOException, ParserException {
    createFileWithText(
        "yml&22/2/2020#2:22#2/3/2020#3:33#descrp&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "yml", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        containsString("is not a valid month"));
  }

  @Test
  void validatorViolationCase2() throws IOException, ParserException {
    createFileWithText(
        "yml&02/02/2020#02:60#2/3/2020#3:33#descrp&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "yml", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        containsString("is not a valid minute"));
  }

  @Test
  void ownerCannotParsedCompletely() throws IOException, ParserException {
    createFileWithText(
        "owner name reached end of file without unescaped chars");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "owner", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("End of file reached before owner been parsed completely"));
  }

  @Test
  void untestedCase1() throws IOException, ParserException {
    createFileWithText(
      "yml&2/2/2020#3:40#2/4/2020#04:3#test file with dir&" +
      "1/09/2020#3:40#2/04/2020#04:3#" +
      "test accessing file with dir02/09/2020#3:40#2/04/2020#04:3#test accessing file with dir&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "yml", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 5);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("An extraneous field encountered to build appointment from file"));
  }
}
