package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.AbstractAppointmentBook;
import edu.pdx.cs410J.AbstractAppointment;
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

    private final TreeSet<T> appts = new TreeSet<T>();
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
     * @param appointment an appointment to be added to the appointment book.
     */
    @Override
    public void addAppointment(T appointment) {
        this.appts.add(appointment);
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
}
