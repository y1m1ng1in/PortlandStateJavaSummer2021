package edu.pdx.cs410J.yl6.database;

import edu.pdx.cs410J.AbstractAppointment;
import edu.pdx.cs410J.AbstractAppointmentBook;
import edu.pdx.cs410J.yl6.*;

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
public interface AppointmentBookStorage {
    /**
     * Get an appointment book that contains all appointments belong to
     * <code>owner</code> in the storage.
     *
     * @param owner the owner name
     * @return an appointment book that contains all appointments with
     *         <code>owner</code>
     * @throws StorageException If any error occurs during read/write with storage
     */
    public AppointmentBook<Appointment> getAllAppointmentsByOwner(String owner) throws StorageException;

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
     * @throws StorageException If any error occurs during read/write with storage
     */
    public AppointmentBook<Appointment> getAppointmentsByOwnerWithBeginInterval(String owner, Date from, Date to)
            throws StorageException;

    /**
     * Insert <code>appointment</code> to the storage.
     *
     * @param owner       the owner of <code>appointment</code>
     * @param appointment the appointment to be stored persistently.
     * @throws StorageException If any error occurs during read/write with storage
     */
    public void insertAppointmentWithOwner(String owner, Appointment appointment) throws StorageException;

    /**
     * Get an appointment book that contains all bookable appointment slots belong
     * to <code>owner</code> in the storage.
     *
     * @param owner the appointment owner
     * @return an appointment book that contains all bookable appointment slots
     * @throws StorageException If any error occurs during read/write with storage
     */
    public AppointmentBook<AppointmentSlot> getAllBookableAppointmentSlotsByOwner(String owner) throws StorageException;

    /**
     * Get an appointment book that contains all appointment slots belong to
     * <code>owner</code> in the storage, include both bookable and non-bookable
     * slots.
     *
     * @param owner the name of the owner
     * @return an appointment book that contains all appointment slots belong to
     *         <code>owner</code>
     * @throws StorageException If any error occurs during read/write with storage
     */
    public AppointmentBook<AppointmentSlot> getAllExistingAppointmentSlotsByOwner(String owner) throws StorageException;

    /**
     * Add an appointment slot that is public bookable to <code>owner</code>
     *
     * @param owner the name of the owner
     * @param slot  an {@link AppointmentSlot} the is bookable
     * @throws StorageException If any error occurs during read/write with storage
     */
    public void insertBookableAppointmentSlot(String owner, AppointmentSlot slot) throws StorageException;

    /**
     * Book an public bookable appointment slot of <code>owner</code>
     *
     * @param owner       the name of the owner
     * @param appointment an {@link Appointment} instance to be booked
     * @throws StorageException If any error occurs during read/write with storage
     */
    public void bookAppointment(String owner, Appointment appointment) throws StorageException;

    /**
     * Insert a {@link User} instance to the storage
     *
     * @param user
     * @throws StorageException
     */
    public void insertUser(User user) throws StorageException;

    /**
     * Retrieve a {@link User} by its username from the storage
     *
     * @param username
     * @return
     * @throws StorageException
     */
    public User getUserByUsername(String username) throws StorageException;

    public boolean verifySlotIsCompatibleWithAll(String owner, AppointmentSlot slot) throws StorageException;

    public boolean verifySlotIsBookable(String owner, AppointmentSlot appointment) throws StorageException;
}
