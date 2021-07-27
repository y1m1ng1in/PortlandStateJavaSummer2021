package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.util.Iterator;

import edu.pdx.cs410J.ParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.Reader;
import java.io.IOException;

/**
 * The class that encapsulates methods communicate with plaintext files stores
 * appointments.
 */
public class PlainTextAsStorage implements AppointmentBookStorage<AppointmentBook<Appointment>, Appointment> {

  private File dir;
  private AppointmentValidator validator = new AppointmentValidator("M/d/yyyy h:m a");

  static final private String userdb = "db_user.txt";

  public PlainTextAsStorage(File dir) {
    this.dir = dir;
    if (!this.dir.exists() || !this.dir.isDirectory()) {
      this.dir.mkdirs();
    }
  }

  /**
   * Create a {@link TextParser} instance given <code>reader</code>. The
   * appointment book parser uses {@link TextAppointmentBookParser}, and
   * appointment parser users {@link TextAppointmentParser} for parser.
   * 
   * @param reader the reader to be used to read from file
   * @return a <code>TextParser</code> instance.
   * @implNote To support futher more complicated appointment book type, it may be
   *           necessary to write other functions to create different types of
   *           parsers that derives {@link TextAppointmentBookParser} and
   *           {@link TextAppointmentParser}, or simply add two parameters for
   *           this method.
   */
  private TextParser createParser(Reader reader) {
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser appointmentParser = new TextAppointmentParser(reader, validator);
    TextParser parser = new TextParser(bookParser, appointmentParser);
    return parser;
  }

  /**
   * Read all the appointments with <code>owner</code>, and create an appointment
   * book that contains all the read appointments with owner name
   * <code>owner</code>.
   * 
   * @param owner the name of the owner of appointments
   * @return an {@link AppointmentBook} that contains all the read appointments if
   *         there exists at least one appointment with <code>owner</code>;
   *         <code>null</code> otherwise.
   * @throws StorageException If file is malformatted such that it can't be
   *                          parsed.
   */
  @Override
  public AppointmentBook<Appointment> getAllAppointmentsByOwner(String owner) throws StorageException {
    File f = new File(this.dir, owner + ".txt");
    Reader reader;
    try {
      reader = new FileReader(f);
    } catch (FileNotFoundException e) {
      return null;
    }

    TextParser parser = createParser(reader);
    AppointmentBook<Appointment> parsed = null;
    try {
      parsed = parser.parse();
    } catch (ParserException e) {
      throw new StorageException("File in storage is malformatted: " + e.getMessage());
    }

    return parsed;
  }

  /**
   * Read all appointments with <code>owner</code> that begins between
   * <code>from</code> and <code>to</code>. If no appointment found that falls
   * into this interval, then this method returns <code>null</code>
   * 
   * @param owner the owner name
   * @param from  the lowerbound of <code>Date</code> instance
   * @param to    the upperbound of <code>Date</code> instance
   * @return an {@link AppointmentBook} that contains all the appointments
   *         satisified; <code>null</code> if nothing found.
   * @throws StorageException If file cannot found, or file is malformatted such
   *                          that it can't be parsed.
   */
  @Override
  public AppointmentBook<Appointment> getAppointmentsByOwnerWithBeginInterval(String owner, Date from, Date to)
      throws StorageException {
    AppointmentBook<Appointment> parsed = getAllAppointmentsByOwner(owner);
    if (parsed == null) {
      return null;
    }
    AppointmentBook<Appointment> satisifed = new AppointmentBook<>(owner);
    for (Appointment appt : parsed.getAppointments()) {
      Date beginAt = appt.getBeginTime();
      if (beginAt.after(from) && beginAt.before(to)) {
        satisifed.addAppointment(appt);
      }
    }
    if (satisifed.getAppointments().isEmpty()) {
      return null;
    }
    return satisifed;
  }

  /**
   * Store {@link Appointment} instance owned by <code>owner</code> to storage.
   * 
   * @param owner       the name of <code>appointment</code> owner
   * @param appointment the appointment to store
   * @throws StorageException If an input or output exception occurs during
   *                          performing on file
   */
  @Override
  public void insertAppointmentWithOwner(String owner, Appointment appointment) throws StorageException {
    File f = locateAppointmentBookFileByOwner(owner);
    AppointmentBook<Appointment> book = null;

    if (f == null) {
      book = new AppointmentBook<>(owner);
      f = new File(this.dir, owner + ".txt");
    } else {
      book = getAllAppointmentsByOwner(owner);
    }

    book.addAppointment(appointment);

    try {
      FileWriter fw = new FileWriter(f);
      TextDumper dumper = new TextDumper(fw);
      dumper.dump(book);
      fw.flush();
      fw.close();
    } catch (IOException e) {
      throw new StorageException("While storing appointment to storage, " + e.getMessage());
    }
  }

  /**
   * Find and create a {@link File} instance that stores appointments owned by
   * <code>owner</code>
   * 
   * @param owner the name of the owner
   * @return a {@link File} instance if found; <code>null</code> otherwise.
   */
  private File locateAppointmentBookFileByOwner(String owner) {
    File[] files = this.dir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String fileName) {
        if (fileName.equals(owner + ".txt")) {
          return true;
        }
        return false;
      }
    });
    if (files.length == 1) {
      return files[0];
    }
    return null;
  }

  /**
   * Insert a <code>user</code> into user storage.
   * 
   * @param user a {@link User} instance to append
   * @throws StorageException If any read/write with file occurs
   */
  public void insertUser(User user) throws StorageException {
    try (FileWriter fw = new FileWriter(new File(this.dir, userdb), true); BufferedWriter bw = new BufferedWriter(fw)) {
      ParseableUserDumper dumper = new ParseableUserDumper(bw);
      dumper.dump(user);
      bw.flush();
    } catch (IOException e) {
      throw new StorageException("While storing user to storage, " + e.getMessage());
    }

  }

  /**
   * Get a {@link User} instance by its username
   * 
   * @param username the name of user
   * @return a {@link User} instance
   * @throws StorageException If any read/write with file occurs
   */
  public User getUserByUsername(String username) throws StorageException {
    File f = new File(this.dir, userdb);
    if (!f.exists()) {
      try {
        f.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try (Reader reader = new BufferedReader(new FileReader(f))) {
      PlainTextIterator<TextUserParser, User> iterator = new PlainTextIterator<>(null, new TextUserParser(reader));
      while (iterator.hasNext()) {
        User user = iterator.next();
        if (user.getUsername().equals(username)) {
          return user;
        }
      }
    } catch (IOException e) {
      throw new StorageException("While retrieving user to storage, " + e.getMessage());
    }
    return null;
  }

  /**
   * A class that works as an iterator that "traverses" a file by invoking
   * {@link Parser#parse}. Thus, it avoids reading the whole file into the memory.
   */
  public class PlainTextIterator<T extends Parser<E>, E> implements Iterator<E> {

    private Parser<?> metaParser;
    private Parser<E> entryParser;
    private E temp;

    /**
     * Create a PlainTextIterator instance, moving the "cursor" of {@link Reader} to
     * the place right after the last character of the meta information, so the next
     * read should be the first character of the first entry
     * 
     * @param metaParser  the parser parses meta information stored in a file
     * @param entryParser the parser parses an individual <code>E</code>
     * @throws StorageException If an input or output exception occurs, or file is
     *                          malformatted
     */
    public PlainTextIterator(Parser<?> metaParser, Parser<E> entryParser) throws StorageException {
      this.metaParser = metaParser;
      this.entryParser = entryParser;
      try {
        if (metaParser != null) {
          metaParser.parse();
        }
      } catch (Exception e) {
        throw new StorageException("While traversing file, " + e.getMessage());
      }
    }

    /**
     * Store the next entry that should be returned by <code>next</code>, if the
     * next entry is null, then it shows that we don't have a "next".
     * 
     * @return <code>true</code> if parsed a <code>E</code> that is not
     *         <code>null</code>; <code>false</code> otherwise
     */
    @Override
    public boolean hasNext() {
      try {
        this.temp = this.entryParser.parse();
      } catch (Exception e) {
        return false;
      }
      return this.temp != null;
    }

    /**
     * Return an instance of <code>E</code> get parsed from the last call to
     * <code>hasNext</code>
     * 
     * @return an instance of <code>E</code> get parsed from the last call to
     *         <code>hasNext</code>
     */
    @Override
    public E next() {
      return this.temp;
    }
  }

}
