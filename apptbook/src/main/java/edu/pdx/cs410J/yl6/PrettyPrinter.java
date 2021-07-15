package edu.pdx.cs410J.yl6;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;

import edu.pdx.cs410J.AppointmentBookDumper;
import edu.pdx.cs410J.AbstractAppointment;
import edu.pdx.cs410J.AbstractAppointmentBook;

public class PrettyPrinter<T extends AbstractAppointmentBook, 
                           E extends AbstractAppointment & PrettyPrintable> 
    implements AppointmentBookDumper<T> {
  
  private Writer writer;
  private String[] fieldNames;
  private final int maxLine = 40;
  private final int tableBoundaryPadding = 2;
  private final String entryDelimiter = fillArray(maxLine, '-') + "\n";
  private final int fieldNameWidth;

  public PrettyPrinter(PrintStream writer, String[] fieldNames) {
    this.writer = new PrintWriter(writer);
    this.fieldNames = fieldNames;
    this.fieldNameWidth = getWidthOfFieldName();
    formatFieldNames();
  }

  public PrettyPrinter(Writer writer, String[] fieldNames) {
    this.writer = new PrintWriter(writer);
    this.fieldNames = fieldNames;
    this.fieldNameWidth = getWidthOfFieldName();
    formatFieldNames();
  }

  public void dump(T book) throws IOException {
    Collection<E> appts = book.getAppointments();
    
    this.writer.write(formatFieldName("Owner"));
    this.writer.write(formatField(book.getOwnerName()));
    this.writer.write("\n");
    this.writer.write(entryDelimiter);
    
    for (E appt : appts) {
      String[] appointmentFields = appt.getPrettyPrinterFields();
      for (int i = 0; i < appointmentFields.length; ++i) {
        this.writer.write(this.fieldNames[i]);
        this.writer.write(formatField(appointmentFields[i]));
        this.writer.write("\n");
      }
      this.writer.write(entryDelimiter);
    }

    this.writer.flush();
    this.writer.close();
  } 

  private void formatFieldNames() {
    for (int i = 0; i < this.fieldNames.length; ++i) {
      this.fieldNames[i] = formatFieldName(this.fieldNames[i]); 
    }
  }

  private String formatFieldName(String s) {
    return String.format("%-" + fieldNameWidth + "s|  ", s);
  }
  
  private int getWidthOfFieldName() {
    int maxLength = 0;
    for (String s : this.fieldNames) {
      if (s.length() > maxLength) {
        maxLength = s.length();
      }
    }
    maxLength += tableBoundaryPadding;
    return maxLength;
  }

  private String fillArray(int length, char value) {
    char[] array = new char[length];
    Arrays.fill(array, value);
    return new String(array);
  }

  private String formatField(String s) {
    StringBuilder sb = new StringBuilder();
    int fieldWidth = maxLine - fieldNameWidth - tableBoundaryPadding;
    int currentLength = fieldWidth;
    String spacesForFieldName = 
        fillArray(fieldNameWidth, ' ') + '|' + fillArray(tableBoundaryPadding, ' ');
    
    if (fieldWidth > s.length()) {
      return s;
    }

    sb.append(s.substring(0, fieldWidth));
    while (currentLength < s.length()) {
      sb.append('\n');
      sb.append(spacesForFieldName);
      if (currentLength + fieldWidth > s.length()) {
        sb.append(s.substring(currentLength, s.length()));
      } else {
        sb.append(s.substring(currentLength, currentLength + fieldWidth));
      }
      currentLength += fieldWidth;
    }

    return sb.toString();
  }
}