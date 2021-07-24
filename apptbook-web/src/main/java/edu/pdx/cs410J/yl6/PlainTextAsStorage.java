package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.util.UUID;

import edu.pdx.cs410J.ParserException;

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

  /**
   * Create a PlainTextAsStorage instance
   * 
   * @param dir the directory path that is to contains files that store
   *            appointment books
   */
  public PlainTextAsStorage(String dir) {
    this.dir = new File(dir);
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

  public void insertUser(User user) throws StorageException {
    try (FileWriter fw = new FileWriter(new File(this.dir, "db_user.txt"), true);
        BufferedWriter bw = new BufferedWriter(fw)) {
      ParseableUserDumper dumper = new ParseableUserDumper(bw);
      dumper.dump(user);
      bw.flush();
    } catch (IOException e) {
      throw new StorageException("While storing user to storage, " + e.getMessage());
    }

  }

  public User getUserById(UUID id) throws StorageException {
    return null;
  }
}
