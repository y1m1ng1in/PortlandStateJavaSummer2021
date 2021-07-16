package edu.pdx.cs410J.yl6;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.AppointmentBookParser;
import edu.pdx.cs410J.AbstractAppointment;
import edu.pdx.cs410J.AbstractAppointmentBook;

/**
 * TextParser is the class parameterized over T which is any derived type from
 * <code>AbstractAppointmentBook</code>, and E which is the element of T such that
 * E is any derived type from <code>AbstractAppointment</code> that implements
 * <code>PlainTextRepresentable</code> interface.
 * <p>
 * TextParser encapsulates method <code>parse</code> which parses owner of appointment
 * book from specified file, then parses each appointment. It is assumed that the owner 
 * and the first appointment is delimited by '&amp;', each appointment is delimited by '&amp;'
 * from other appointments. Each field of appointment is delimited by '#'.
 * <p>
 * TextParser intends to build a <code>T</code> by adding each parsed <code>E</code>. 
 * <code>E</code> is built by passing in <code>expectedNumberofField</code> number of 
 * strings to <code>E</code>'s constuctor (which means that E must support such 
 * construction, otherwise exception will raise). The order of arguments passed into 
 * <code>E</code>'s constuctor is same as the order appear in the file for each appointment.
 * <code>expectedNumberofField</code> is specified via <code>TextParser</code> constructor.
 */
public class TextParser<T extends AbstractAppointmentBook, 
                        E extends AbstractAppointment & PlainTextRepresentable> 
    implements AppointmentBookParser<T> {

  private String filename;
  private String owner;
  private StringBuilder sb;
  private String[] appointmentArguments;
  private int currentArgIndex;
  private Class<T> bookClass;
  private Class<E> apptClass;
  private AppointmentValidator validator;
  private AbstractValidator ownerValidator;
  private final int expectedNumberofField;

  static final String EOF_REACHED_PARSE_ARG = 
      "End of file reached before the last appointment been parsed completely";
  static final String EOF_REACHED_PARSE_OWNER = 
      "End of file reached before owner been parsed completely";
  static final String PROHIBIT_CHAR_IN_OWNER = 
      "Prohibited character # occurs when parsing owner name.";
  static final String NOT_ENOUGH_FIELD = "Not enough fields to build appointment from file";
  static final String OWNER_MISMATCH = "Owner parsed from file is mismatched with argument: ";
  static final String CANNOT_FIND_FILE = "Cannot find file: ";
  static final String IOEXCEPTION_OCCUR = "IOException occurs during parsing with message: ";
  static final String PROGRAM_INTERNAL_ERROR = "Program internal error: ";
  static final String MORE_FIELD_THAN_NEEDED = 
      "An extraneous field encountered to build appointment from file";

  /**
   * Create a TextParser instance. 
   * 
   * @param filename              
   *        the relative path of the file to be parsed
   * @param owner                 
   *        a String of appointment book owner
   * @param bookClass             
   *        the <code>Class</code> of appointment book
   * @param apptClass             
   *        the <code>Class</code> of appointment
   * @param ownerValidator
   *        the validator that derived from <code>AbstractValidator</code> applied to be 
   *        applied to owner field of the appointment book
   * @param validator            
   *        validator, a <code>AppointmentValidator</code> instance that validates 
   *        fields get parsed from file to build appointment.  
   * @param expectedNumberofField 
   *        the number of fields expect to be parsed for every appointment
   */
  public TextParser(
      String filename, 
      String owner, 
      Class<T> bookClass, 
      Class<E> apptClass,
      AbstractValidator ownerValidator, 
      AppointmentValidator validator, 
      int expectedNumberofField) {
    this.filename = filename;
    this.owner = owner;
    this.bookClass = bookClass;
    this.apptClass = apptClass;
    this.ownerValidator = ownerValidator;
    this.validator = validator;
    this.expectedNumberofField = expectedNumberofField;
    this.sb = new StringBuilder();
    this.appointmentArguments = new String[this.expectedNumberofField];
    this.currentArgIndex = 0;
  }
  
  /** 
   * Parse the file and build an appointment book. The procedure is that parse owner of the 
   * appointment book first, then parse appointments. If end-of-file reached before owner 
   * name is completely parsed (encountered a '&amp;' which does not follow a '\'), or before a 
   * complete appointment is parsed (<code>expectedNumberofField</code> number of fields are 
   * parsed completely, which means for each field a '#' which does not follow a '\' 
   * encountered; also encountered a '&amp;' which does not follow a '\' after all fields parsed), 
   * then <code>ParserException</code> is thrown. 
   * <p>
   * Also, <code>validators</code> passed in from constructor apply, where ith validator 
   * validates ith field of the appointment once an appointment get completely parsed. Once 
   * violation detected, <code>ParserException</code> is thrown immediately, 
   * 
   * @return                 a appointment book 
   * @throws ParserException expcetion due to incorrect format of input file, or violation 
   *                         detected when applying validators
   */
  public T parse() throws ParserException {
    File f;
    Reader reader;
    T book;
    String owner;

    try {
      // create a new file and return if file is not exist
      f = new File(this.filename);
      if (!f.isFile()) {
        return this.bookClass
            .getDeclaredConstructor(String.class)
            .newInstance(this.owner);
      }
      reader = new FileReader(this.filename);

      // parse owner and create book
      owner = parseOwner(reader);
      book = this.bookClass
          .getDeclaredConstructor(String.class)
          .newInstance(owner);

      // fill appoinments in to the book
      book = parseAppointments(reader, book);

      reader.close();
    } catch (ParserException ex) {
      throw ex;
    } catch (IOException ex) {
      throw new ParserException(ex.getMessage());
    } catch (Exception ex) {
      throw new ParserException(PROGRAM_INTERNAL_ERROR + ex.getMessage());
    }

    return book;
  }
  
  /** 
   * Parse owner of the appointment book from input file
   * 
   * @param reader           the <code>Reader</code> instance used for parsing
   * @return                 a String of owner parsed
   * @throws IOException     exception raise duing to read from file 
   * @throws ParserException incorrect format of the file, or owner of the book passed 
   *                         into the instance is different from being parsed
   */
  private String parseOwner(Reader reader) throws IOException, ParserException {
    char c = ' ';
    int next;

    parsing: while ((next = reader.read()) != -1) {
      c = (char) next;
      switch (c) {
        case '&':
          break parsing;
        case '#': // '#' must be escaped by '\#'
          throw new ParserException(PROHIBIT_CHAR_IN_OWNER);
        case '\\':
          next = reader.read();
          if (next != -1) {
            c = (char) next;
          } 
        default:
          this.sb.append(c);
      }
    }
    if (c != '&') {
      throw new ParserException(EOF_REACHED_PARSE_OWNER);
    }

    String owner = this.sb.toString();
    
    if (!this.ownerValidator.isValid(owner)) {
      throw new ParserException(this.ownerValidator.getErrorMessage());
    }
    if (!owner.equals(this.owner)) {
      throw new ParserException(OWNER_MISMATCH + owner + " versus " + this.owner);
    }
    this.sb.setLength(0);
    
    return owner;
  }

  /**
   * 
   * @param reader
   * @param book
   * @return
   * @throws ParserException
   * @throws IOException
   */
  private T parseAppointments(Reader reader, T book) throws ParserException, IOException {
    int next;
    char c = ' ';

    parsing: while ((next = reader.read()) != -1) {
      c = (char) next;
      switch (c) {
        case '#':
          if (this.currentArgIndex == this.expectedNumberofField) {
            throw new ParserException(MORE_FIELD_THAN_NEEDED);
          }
          placeArgumentAndResetStringBuilder();
          this.currentArgIndex += 1;
          break;

        case '&':
          placeArgumentAndResetStringBuilder();
          if (this.currentArgIndex != this.expectedNumberofField - 1) {
            throw new ParserException(
                NOT_ENOUGH_FIELD + " expect " + this.expectedNumberofField + 
                ", but got " + (this.currentArgIndex + 1));
          }
          this.currentArgIndex = 0;
          book.addAppointment(buildAppointment());
          break;

        case '\\':
          next = reader.read(); 
          if (next != -1) {
            this.sb.append((char) next);
          } 
          break;

        default:
          this.sb.append(c);
      }
    }

    if (c != '&' || this.currentArgIndex != 0) {
      throw new ParserException(EOF_REACHED_PARSE_ARG);
    }

    return book;
  }

  /** 
   * Construct a appointment instance from fields parsed from file. 
   * 
   * @return                 a appointment instance
   * @throws ParserException violation detected from validators
   */
  private E buildAppointment() throws ParserException {
    if (!this.validator.isValid(this.appointmentArguments)) {
      throw new ParserException(this.validator.getErrorMessage());
    }

    Class[] ts = new Class[this.expectedNumberofField];
    Arrays.fill(ts, String.class);

    // All the error due to invalid fields parsed from file should be catched
    // above, any exception get caught is error caused by programmer
    try {
      return this.apptClass
          .getDeclaredConstructor(ts)
          .newInstance((Object[]) this.appointmentArguments);
    } catch (Exception ex) {
      throw new ParserException(PROGRAM_INTERNAL_ERROR + ex.getMessage());
    }
  }

  /**
   * Place the parsed string (stored in a string builder <code>sb</code>) into 
   * <code>appointmentArguments</code> array which stores the arguments to be passed into 
   * appointment's constructor, which is done in <code>buildAppointment</code> method. 
   * And clear <code>sb</code> to store intermediate results of the next field during parsing.
   */
  private void placeArgumentAndResetStringBuilder() {
    this.appointmentArguments[this.currentArgIndex] = this.sb.toString();
    this.sb.setLength(0);
  }

}