package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.ParseException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PrettyPrinterTest {
  
  String prettyFile = "prettyfile.txt";
  String[] fields = { "Begin at", "End at", "Description", "Duration" };

  String readFile() throws IOException {
    Reader reader = new FileReader(prettyFile);
    StringBuilder sb = new StringBuilder();
    int c;
    while((c = reader.read()) != -1) {
      sb.append((char) c);
    }
    reader.close();
    return sb.toString();
  }

  @Test
  void testCase1() throws ParseException, IOException {
    PrettyPrinter<AppointmentBook<Appointment>, Appointment> printer = 
        new PrettyPrinter(new FileWriter(prettyFile), fields);
    AppointmentBook book = new AppointmentBook("a owner");
    Appointment appointment1 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy1");
    Appointment appointment2 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy2");
    book.addAppointment(appointment1);
    book.addAppointment(appointment2);
    printer.dump(book);
    String s = readFile();
    String exp = 
        "Owner        |  a owner\n" +
        "----------------------------------------\n" +
        "Begin at     |  3/14/20, 4:29 PM\n" + 
        "End at       |  3/14/20, 4:50 PM\n" +
        "Description  |  dummy1\n" +
        "Duration     |  21 minutes\n" + 
        "----------------------------------------\n" +
        "Begin at     |  3/14/20, 4:29 PM\n" + 
        "End at       |  3/14/20, 4:50 PM\n" +
        "Description  |  dummy2\n" +
        "Duration     |  21 minutes\n" +
        "----------------------------------------\n";
    assertThat(s, equalTo(exp));
  }

  @Test
  void testCase2() throws ParseException, IOException {
    PrettyPrinter<AppointmentBook<Appointment>, Appointment> printer = 
        new PrettyPrinter(new FileWriter(prettyFile), fields);
    AppointmentBook book = new AppointmentBook("a owner");
    Appointment appointment1 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy1");
    Appointment appointment2 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy2");
    Appointment appointment3 = new Appointment("1/12/2020 5:30 pm","1/12/2020 6:50 pm",
        "supersupersuper supersupersuper supersupersuper longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglong description");
    book.addAppointment(appointment1);
    book.addAppointment(appointment2);
    book.addAppointment(appointment3);
    printer.dump(book);
    String s = readFile();
    String exp = 
        "Owner        |  a owner\n" +
        "----------------------------------------\n" +
        "Begin at     |  1/12/20, 5:30 PM\n" + 
        "End at       |  1/12/20, 6:50 PM\n" +
        "Description  |  supersupersuper supersup\n"+
        "             |  ersuper supersupersuper \n" + 
        "             |  longlonglonglonglonglong\n" + 
        "             |  longlonglonglonglonglong\n" + 
        "             |  longlonglonglonglonglong\n" + 
        "             |   description\n" +
        "Duration     |  80 minutes\n" + 
        "----------------------------------------\n" +
        "Begin at     |  3/14/20, 4:29 PM\n" + 
        "End at       |  3/14/20, 4:50 PM\n" +
        "Description  |  dummy1\n" +
        "Duration     |  21 minutes\n" + 
        "----------------------------------------\n" +
        "Begin at     |  3/14/20, 4:29 PM\n" + 
        "End at       |  3/14/20, 4:50 PM\n" +
        "Description  |  dummy2\n" +
        "Duration     |  21 minutes\n" +
        "----------------------------------------\n";
    assertThat(s, equalTo(exp));
  }

}