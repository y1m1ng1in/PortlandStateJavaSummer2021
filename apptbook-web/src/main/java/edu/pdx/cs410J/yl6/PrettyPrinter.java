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

/**
 * PrettyPrinter is the class that creates a nicely-formatted textual presentation 
 * of an appointment book, where appointment book can be any type that derives
 * {@link AbstractAppointmentBook}, and appointments of appointment book can be
 * any type that derives {@link AbstractAppointment} and implements interface
 * {@link PrettyPrintable}.
 * <p>
 * This class functions printing by invoking method <code>getPrettyPrinterFields</code>
 * from interface <code>PrettyPrintable</code> that is required for each appointment. 
 * The order of fields get dumped is same as the order returned by 
 * <code>getPrettyPrinterFields</code> from low index to high index. The actual 
 * formatting is performmed by this class internally. 
 */
public class PrettyPrinter<T extends AbstractAppointmentBook, 
                           E extends AbstractAppointment & PrettyPrintable> 
    implements AppointmentBookDumper<T> {
  
  private Writer writer;
  private String[] fieldNames;
  private final int maxLine = 40;
  private final int tableBoundaryPadding = 2;
  private final String entryDelimiter = fillArray(maxLine, '-') + "\n";
  private final int fieldNameWidth;

  /**
   * Create a PrettyPrinter instance.
   * 
   * @param writer     the {@link PrintStream} instance that preform writing
   * @param fieldNames an array of strings that to be displayed for each field's name
   */
  public PrettyPrinter(PrintStream writer, String[] fieldNames) {
    this.writer = new PrintWriter(writer);
    this.fieldNames = fieldNames;
    this.fieldNameWidth = getWidthOfFieldName();
    formatFieldNames();
  }

  /**
   * Create a PrettyPrinter instance.
   * 
   * @param writer     the {@link Writer} instance that preform writing
   * @param fieldNames an array of strings that to be displayed for each field's name
   */
  public PrettyPrinter(Writer writer, String[] fieldNames) {
    this.writer = new PrintWriter(writer);
    this.fieldNames = fieldNames;
    this.fieldNameWidth = getWidthOfFieldName();
    formatFieldNames();
  }

  
  /** 
   * Dump a nicely-formatted textual presentation of <code>book</code>
   * 
   * @param book         an appointment book to be dumped, the type of appointment book
   *                     can be any type that derives <code>AbstractAppointmentBook</code>
   * @throws IOException any exception raise from input/output from file
   */
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

  
  /** 
   * Create a string that display the name of the field, which a vertical bar
   * that delimits the name of the field from the actual field content.
   * 
   * @param s the name of the field
   * @return  a string that represents the name of the field that is padded
   *          with extra spaces and a vertical bar appended at the end, which
   *          makes it to be aligned with rest of the name of the fields
   */
  private String formatFieldName(String s) {
    return String.format("%-" + fieldNameWidth + "s|  ", s);
  }
  
  
  /** 
   * Calculate the length of the string for name of the fields.
   * 
   * @return the length of the string for name of the fields
   */
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

  
  /** 
   * Construct a string that is formed by same character, <code>value</code>, 
   * with length. <code>length</code>.
   * 
   * @param length the length of the String
   * @param value  the value of each element to be filled
   * @return       a string that is formed with same character
   */
  private String fillArray(int length, char value) {
    char[] array = new char[length];
    Arrays.fill(array, value);
    return new String(array);
  }

  
  /** 
   * Format a string that is too long, such that the resulting string contains
   * multiple lines and the length of each line is limited by <code>maxLine</code>
   * which is a fixed value preset by this class. 
   * 
   * @param s the string to be formatted
   * @return  the formatted string
   */
  private String formatField(String s) {
    StringBuilder sb = new StringBuilder();
    int fieldWidth = maxLine - fieldNameWidth - 1 - tableBoundaryPadding;
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