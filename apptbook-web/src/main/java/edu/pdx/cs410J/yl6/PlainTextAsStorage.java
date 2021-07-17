package edu.pdx.cs410J.yl6;

import java.util.Date;

import edu.pdx.cs410J.ParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.Reader;
import java.io.IOException;

public class PlainTextAsStorage implements AppointmentBookStorage<AppointmentBook<Appointment>, Appointment> {


  private static final String PROGRAM_INTERNAL_ERROR = "Program internal error: ";
  private static final String IOEXCEPTION_OCCUR = "While parsing file, ";

  private File dir;
  private AppointmentValidator validator = new AppointmentValidator("M/d/yyyy h:m a");
  private String errorMessage;

  public PlainTextAsStorage(String dir) {
    this.dir = new File(dir);
  }

  @Override
  public AppointmentBook<Appointment> getAllAppointmentsByOwner(String owner) {
    File f = locateAppointmentBookFileByOwner(owner);

    if (f == null) {
      return null;
    }

    Reader reader;
    try {
      reader = new FileReader(f);
    } catch (FileNotFoundException e) {
      this.errorMessage = PROGRAM_INTERNAL_ERROR + e.getMessage();  
      return null;
    }
    
    TextParser parser = createParser(reader);
    AppointmentBook<Appointment> parsed = null;
    try {
      parsed = parser.parse();
    } catch (ParserException e) {
      this.errorMessage = IOEXCEPTION_OCCUR + e.getMessage();
    }

    return parsed;
  }

  private TextParser createParser(Reader reader) {
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser appointmentParser = new TextAppointmentParser(reader, validator);
    TextParser parser = new TextParser(bookParser, appointmentParser);
    return parser;
  }

  @Override
  public AppointmentBook<Appointment> getAppointmentsByOwnerWithBeginInterval(String owner, Date from, Date to) {
    try {
      AppointmentBook<Appointment> parsed = getAllAppointmentsByOwner(owner);
      AppointmentBook<Appointment> satisifed = new AppointmentBook<>(owner);
      for (Appointment appt : parsed.getAppointments()) {
        Date beginAt = appt.getBeginTime();
        if (beginAt.after(from) && beginAt.before(to)) {
          satisifed.addAppointment(appt);
        }
      }
      return satisifed;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public boolean insertAppointmentWithOwner(String owner, Appointment appointment) {
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
      TextDumper<AppointmentBook<Appointment>, Appointment> dumper = new TextDumper<>(fw);
      dumper.dump(book);
      fw.flush();
      fw.close();
    } catch (IOException e) {
      this.errorMessage = IOEXCEPTION_OCCUR + e.getMessage();
      return false;
    }

    return true;
  }

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

  public String getErrorMessage() {
    return errorMessage;
  }
}
