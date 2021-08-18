package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.AbstractAppointmentBook;
import edu.pdx.cs410J.AbstractAppointment;

import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

/**
 * AppointmentBook is the class that collects a collection of appointments
 * belong to a owner. Each appointment in <code>AppointmentBook</code> is a
 * subclass derived by <code>AbstractAppointment</code>. The client program can
 * create an empty appointment book specified by its owner, add appointment, get
 * a reference to the collection of appointment, and get owner name of the
 * appointment book via provided public methods.
 */
public class AppointmentBook<T extends AbstractAppointment> extends AbstractAppointmentBook<T> {

  private final TreeSet<T> appts = new TreeSet<T>(new NonOverlappingConstrainter());
  private final String owner;

  /**
   * Construct an empty appointment book with specified <code>owner</code>.
   * 
   * @param owner the name of the owner.
   */
  public AppointmentBook(String owner) {
    this.owner = owner;
  }

  /**
   * Add a appointment to the appointment book.
   * 
   * @param appt an appointment to be added to the appointment book.
   */
  @Override
  public void addAppointment(T appt) {
    this.appts.add(appt);
  }

  /**
   * Return all the appointments in the appointment book.
   * 
   * @return a reference to the <code>TreeSet</code> of the appointments in this
   *         appointment book
   */
  @Override
  public TreeSet<T> getAppointments() {
    return this.appts;
  }

  /**
   * Returns the name of the owner of this appointment book.
   * 
   * @return the name of the owner
   */
  @Override
  public String getOwnerName() {
    return this.owner;
  }

  /**
   * Checks if <code>slot</code> conflicts with existing appointments.
   * 
   * @param slot the appointment time slot to check
   * @return <code>true</code> if conflicts; <code>false</code> otherwise
   */
  public boolean contains(T slot) {
    return this.appts.contains(slot);
  }

  public class NonOverlappingConstrainter implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
      Date o1Begin = o1.getBeginTime();
      Date o2Begin = o2.getBeginTime();
      Date o1End = o1.getEndTime();
      Date o2End = o2.getEndTime();

      if (o1Begin.after(o2End)) {
        // o2Begin < o2End < o1Begin < o1End
        return 1;
      } else if (o1End.before(o2Begin)) {
        // o1Begin < o1End < o2Begin < o2End
        return -1;
      } else {
        // two appointment slots do not overlapped if and only if as two cases described
        // above
        return 0;
      }
    }
  }
}
