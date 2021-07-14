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
    NonemptyStringValidator descriptionValidator = new NonemptyStringValidator("description");
    DateTimeStringValidator dtValidator = new DateTimeStringValidator("M/d/yyyy h:m a");
    AbstractValidator[] validators = {
      dtValidator, dtValidator, descriptionValidator
    };
    return validators;
  }

  /**
   * Tests that correct begin date and time is returned as a string.
   */
  @Test
  void wellFormattedWithOneAppt() throws IOException, ParserException {
    createFileWithText("yml&2/2/2020 2:22 am#2/3/2020 3:33 am#descrp&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "yml", AppointmentBook.class, Appointment.class, 
                     new NonemptyStringValidator("owner"), createValidators(), 3);
    AppointmentBook<Appointment> book = textParser.parse();  
    Appointment inBook = book.getAppointments().get(0);
    
    String s = inBook.getBeginTimeString();
    assertThat(s, equalTo("2/2/20, 2:22 AM"));
    s = inBook.getEndTimeString();
    assertThat(s, equalTo("2/3/20, 3:33 AM"));
    s = inBook.getDescription();
    assertThat(s, equalTo("descrp"));
  }

  @Test
  void wellFormattedWithTwoAppts() throws IOException, ParserException {
    createFileWithText(
        "yml&2/2/2020 2:22 pm#2/3/2020 3:33 pm#descrp&11/14/2021 12:30 am#11/15/2021 2:45 pm#desc  des ddd &");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "yml", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 3);
    AppointmentBook<Appointment> book = textParser.parse();  
    Appointment inBook1 = book.getAppointments().get(0);
    Appointment inBook2 = book.getAppointments().get(1);

    String s = inBook1.getBeginTimeString();
    assertThat(s, equalTo("2/2/20, 2:22 PM"));
    s = inBook1.getEndTimeString();
    assertThat(s, equalTo("2/3/20, 3:33 PM"));
    s = inBook1.getDescription();
    assertThat(s, equalTo("descrp"));

    s = inBook2.getBeginTimeString();
    assertThat(s, equalTo("11/14/21, 12:30 AM"));
    s = inBook2.getEndTimeString();
    assertThat(s, equalTo("11/15/21, 2:45 PM"));
    s = inBook2.getDescription();
    assertThat(s, equalTo("desc  des ddd "));
  }

  @Test
  void wellFormattedWithEscapedCharWithOneAppt() throws IOException, ParserException {
    createFileWithText(
        "\\#\\#\\#\\&\\&\\&\\#\\#\\#&2/2/2020 2:22 pm#2/3/2020 3:33 pm#\\&\\&des\\&c\\&\\&\\&\\&&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "###&&&###", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 3);
    AppointmentBook<Appointment> book = textParser.parse();  
    Appointment inBook1 = book.getAppointments().get(0);

    String s = inBook1.getBeginTimeString();
    assertThat(s, equalTo("2/2/20, 2:22 PM"));
    s = inBook1.getEndTimeString();
    assertThat(s, equalTo("2/3/20, 3:33 PM"));
    s = inBook1.getDescription();
    assertThat(s, equalTo("&&des&c&&&&"));
  }

  @Test
  void missingAmpersandInTheEnd() throws IOException, ParserException {
    createFileWithText(
        "\\#\\#\\#\\\\\\\\\\#\\#\\#&2/2/2020 2:22 pm#2/3/2020 3:33 pm#\\&\\&des\\&c\\&\\&\\&\\");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "###\\\\###", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 3);
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
                      new NonemptyStringValidator("owner"), createValidators(), 3);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("Not enough fields to build appointment from file expect 3, but got 2"));
  }

  @Test
  void missingOwner() throws IOException, ParserException {
    createFileWithText(
        "2/2/2020 2:22 2/3/2020 3:33#\\&\\&des\\&c\\&\\&\\&\\&&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "###\\\\###", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 3);
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
                      new NonemptyStringValidator("owner"), createValidators(), 3);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("Not enough fields to build appointment from file expect 3, but got 1"));
  }

  @Test
  void onlyOwner() throws IOException, ParserException {
    createFileWithText(
        "owner&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "owner", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 3);
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
                      new NonemptyStringValidator("owner"), createValidators(), 3);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        containsString("Owner parsed from file is mismatched with argument"));
  }

  @Test
  void fileNotExist() throws IOException, ParserException {
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser("notexist.txt", "owner1", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 3);
    AppointmentBook book = textParser.parse();
    assertThat(book.getAppointments().size(), equalTo(0));
    File f = new File("notexist.txt");
    f.delete();
  }

  @Test
  void validatorViolationCase1() throws IOException, ParserException {
    createFileWithText(
        "yml&22/2/2020 2:22 pm#2/3/2020 3:33 pm#descrp&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "yml", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 3);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        containsString("Unparseable date"));
  }

  @Test
  void validatorViolationCase2() throws IOException, ParserException {
    createFileWithText(
        "yml&02/02/2020 02:60 am#2/3/2020 3:33 am#descrp&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "yml", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 3);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        containsString("Unparseable date"));
  }

  @Test
  void ownerCannotParsedCompletely() throws IOException, ParserException {
    createFileWithText(
        "owner name reached end of file without unescaped chars");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "owner", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 3);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("End of file reached before owner been parsed completely"));
  }

  @Test
  void untestedCase1() throws IOException, ParserException {
    createFileWithText(
      "yml&2/2/2020 3:40 am#2/4/2020 04:3 am#test file with dir&" +
      "1/09/2020 3:40 am#2/04/2020 04:3 am#" +
      "test accessing file with dir02/09/2020 3:40 pm#2/04/2020 04:3 pm#test accessing file with dir&");
    TextParser<AppointmentBook, Appointment> textParser =
      new TextParser(testFile, "yml", AppointmentBook.class, Appointment.class, 
                      new NonemptyStringValidator("owner"), createValidators(), 3);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("An extraneous field encountered to build appointment from file"));
  }
}
