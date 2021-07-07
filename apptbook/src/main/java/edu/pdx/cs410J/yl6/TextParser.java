package edu.pdx.cs410J.yl6;

import java.io.IOException;
import java.io.Reader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.AppointmentBookParser;
import edu.pdx.cs410J.AbstractAppointment;
import edu.pdx.cs410J.AbstractAppointmentBook;

public class TextParser<T extends AbstractAppointmentBook, E extends AbstractAppointment> 
    implements AppointmentBookParser<T> {
  private String filename;
  private String owner;
  private StringBuilder sb;
  private String[] appointmentArguments;
  private int currentArgIndex;
  private Class<T> bookClass;
  private Class<E> apptClass;

  static final String EOF_REACHED_PARSE_ARG = 
      "End of file reached before the last appointment been parsed completely";
  static final String EOF_REACHED_PARSE_OWNER = 
      "End of file reached before owner been parsed completely";
  static final String OWNER_MISMATCH = "Owner parsed from file is mismatched with argument: ";
  static final String CANNOT_FIND_FILE = "Cannot find file: ";
  static final String IOEXCEPTION_OCCUR = "IOException occurs during parsing with message: ";
  static final String PROGRAM_INTERNAL_ERROR = "Program internal error: ";

  public TextParser(String filename, String owner, Class<T> bookClass, Class<E> apptClass) {
    this.filename = filename;
    this.owner = owner;
    this.sb = new StringBuilder();
    this.appointmentArguments = new String[3];
    this.currentArgIndex = 0;

    ClassLoader cl = ClassLoader.getSystemClassLoader();
    this.bookClass = bookClass;
    this.apptClass = apptClass;
  }
  
  public T parse() throws ParserException {
    int next;
    char c = ' ';

    try {
      File f = new File(this.filename);
      if (!f.isFile()) {
        f.createNewFile();
        return this.bookClass
            .getDeclaredConstructor(String.class)
            .newInstance(this.owner);
      }

      Reader reader = new FileReader(this.filename);
      String owner = parseOwner(reader);
      T book = this.bookClass
          .getDeclaredConstructor(String.class)
          .newInstance(owner);

      while ((next = reader.read()) != -1) {
        c = (char) next;
        if (c == '#') {
          placeArgumentAndResetStringBuilder();
          this.currentArgIndex += 1;
        } else if (c == '&') {
          placeArgumentAndResetStringBuilder();
          this.currentArgIndex = 0;
          book.addAppointment(buildAppointment());
        } else {
          this.sb.append(c);
        }
      }
      if (c != '&' || this.currentArgIndex != 0) {
        throw new ParserException(EOF_REACHED_PARSE_ARG);
      }
      reader.close();
      return book;
    } catch (ParserException ex) {
      throw ex;
    } catch (IOException ex) {
      throw new ParserException(IOEXCEPTION_OCCUR + ex.getMessage());
    } catch (Exception ex) {
      throw new ParserException(PROGRAM_INTERNAL_ERROR + ex.toString());
    } 
  }

  private String parseOwner(Reader reader) throws IOException, ParserException {
    char c = ' ';
    int next;
    while ((next = reader.read()) != -1) {
      c = (char) next;
      if (c == '&') {
        break;
      }
      this.sb.append(c);
    }
    if (c != '&') {
      throw new ParserException(EOF_REACHED_PARSE_OWNER);
    }
    String owner = this.sb.toString();
    if (!owner.equals(this.owner)) {
      throw new ParserException(OWNER_MISMATCH + owner + " versus " + this.owner);
    }
    this.sb.setLength(0);
    return owner;
  }

  private E buildAppointment() throws ParserException {
    String[] beginData = this.appointmentArguments[0].split("\\s");
    String[] endData = this.appointmentArguments[1].split("\\s");
    String description = this.appointmentArguments[2];
    try {
      return this.apptClass
          .getDeclaredConstructor(String.class, String.class, String.class,
              String.class, String.class)
          .newInstance(beginData[0], beginData[1], endData[0], 
              endData[1], description);
    } catch (Exception ex) {
      throw new ParserException(PROGRAM_INTERNAL_ERROR + ex.toString());
    }
  }

  private void placeArgumentAndResetStringBuilder() {
    this.appointmentArguments[this.currentArgIndex] = this.sb.toString();
    this.sb.setLength(0);
  }

}