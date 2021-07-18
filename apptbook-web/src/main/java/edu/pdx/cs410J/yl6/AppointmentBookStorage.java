package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.AbstractAppointment;
import edu.pdx.cs410J.AbstractAppointmentBook;

import java.util.Date;

/**
 * This interface is to be implemented by classes that communicates between
 * persistent storage (a collection of files, or a database server, etc). The
 * interface describes the functionalities need to be implemented to support
 * application.
 * <p>
 * The interface is parameterized over <code>T</code> which is any type that
 * derives {@link AbstractAppointmentBook}, and <code>E</code> which is the type
 * of appointment to be stored in <code>T</code>, which can be any type that
 * derives {@link AbstractAppointment}.
 */
public interface AppointmentBookStorage<T extends AbstractAppointmentBook<E>, E extends AbstractAppointment> {

  /**
   * Get an appointment book that contains all appointments belong to
   * <code>owner</code> in the storage.
   * 
   * @param owner the owner name
   * @return an appointment book that contains all appointments with
   *         <code>owner</code>
   */
  public T getAllAppointmentsByOwner(String owner);

  /**
   * Retrieve the storage to get a collection of appointments that begin between
   * <code>from</code> to <code>to</code>. The resulting appointments are stored
   * in <code>T</code>.
   * 
   * @param owner the owner name of the appointments to be retrieved
   * @param from  the earliest begin time of appointments to be retrieved
   * @param to    the latest begin time of appointments to be retrieved
   * @return an appointment book <code>T</code> that contains all the appointments
   *         whose begin time is between <code>from</code> to <code>to</code>.
   */
  public T getAppointmentsByOwnerWithBeginInterval(String owner, Date from, Date to);

  /**
   * Insert <code>appointment</code> to the storage.
   * 
   * @param owner       the owner of <code>appointment</code>
   * @param appointment the appointment to be stored persistently.
   * @return <code>true</code> if stored successfully; <code>false</code>
   *         otherwise.
   */
  public boolean insertAppointmentWithOwner(String owner, E appointment);
}