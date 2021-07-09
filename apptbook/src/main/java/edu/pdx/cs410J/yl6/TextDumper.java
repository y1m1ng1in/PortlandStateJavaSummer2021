package edu.pdx.cs410J.yl6;

import java.util.Collection;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;

import edu.pdx.cs410J.AppointmentBookDumper;
import edu.pdx.cs410J.AbstractAppointment;
import edu.pdx.cs410J.AbstractAppointmentBook;

/**
 * TextDumper is the class parameterized over T which is any derived type from
 * <code>AbstractAppointmentBook</code>, and E which is the element of T such that
 * E is any derived type from <code>AbstractAppointment</code> that implements
 * <code>PlainTextRepresentable</code> interface.
 * <p>
 * TextDumper encapsulates method <code>dump</code> which writes owner of appointment
 * book to specified file, followed by <code>E</code>'s fields delimited by '#', each
 * <code>E</code> is delimited by '&'. Between the owner and the first appointment 
 * written, it is delimited by '&', too.
 * <p>
 * TextDumper also detects any character that conflicts with delimiters, thus it adds 
 * '\' before any conflicted character, also adds '\' before '\'. 
 * <p>
 * The order of each field of an appointment written to file is same as what 
 * <code>getStringFields</code> returned from low index to high index, which is a method 
 * in interface <code>PlainTextRepresentable</code>.
 */
public class TextDumper<T extends AbstractAppointmentBook, 
                        E extends AbstractAppointment & PlainTextRepresentable> 
    implements AppointmentBookDumper<T> {

  private String filename;

  /**
   * Create a TextDumper instance with specified relative path of the file to be written.
   * 
   * @param filename the relative path of the file to be written to
   */
  public TextDumper(String filename) {
    this.filename = filename;
  }

  /**
   * Write <code>book</code> to file. The owner of the <code>book</code> is followed by 
   * each appointments, delimited by '&'. Between appointment and appointment, it is also
   * delimited by '&'. Each field of an appointment is delimited by '#'. The order of the 
   * field is determined by what is returned from <code>getStringFields</code>, which must
   * be implemented in any <code>E</code> from low index to high index. Any character 
   * conflicts with delimiter is added a '\' before it, also '\' is added a '\' before it. 
   * <code>TextParser</code> is able to recognize '\'-ed characters and recover it to original 
   * character. 
   * 
   * @param book         the appointment book to be written to file
   * @throws IOException any <code>IOException</code> occurs during writing to file
   */
  @Override
  public void dump(T book) throws IOException {
    try {
      Writer writer = new FileWriter(this.filename);
      Collection<E> appts = book.getAppointments();
      String owner = book.getOwnerName();
      writer.write(addEscapeCharacter(owner));
      writer.write('&');
      for (E appt: appts) {
        String[] appointmentFields = appt.getStringFields();
        for (int i = 0; i < appointmentFields.length; ++i) {
          writer.write(addEscapeCharacter(appointmentFields[i]));
          if (i + 1 == appointmentFields.length) {
            writer.write('&');
          } else {
            writer.write('#');
          }
        }
      }
      writer.flush();
      writer.close();
    } catch (IOException ex) {
      throw ex;
    }
  }

  /**
   * Scan <code>s<\code> and add '\' before any character that conflicts with delimiters and '\'
   * 
   * @param s the string to be scanned and add '\' before any character that conflicts with 
   *          delimiters and '\'.
   * @return  a string that has been processed as above. 
   */
  private String addEscapeCharacter(String s) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (c == '#' || c == '&' || c == '\\') {
        sb.append('\\');
      } 
      sb.append(c);
    }
    return sb.toString();
  }
}