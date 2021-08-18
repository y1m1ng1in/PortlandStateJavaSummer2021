package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.AbstractAppointment;

import java.util.Date;
import java.util.UUID;

/**
 * AppointmentSlot is the class that represents the meta information of an appointment, which include its
 * id, owner name, begin and end time, and the type of the slot (which can be open to every one to book,
 * already booked by some one, and so on... as described by {@link SlotType}. Also includes the participator
 * information, which could be <code>null</code> if no participator information included, typically this happens
 * when the slot is currently opened for every one to book, or it is a owner self-added slot, as described
 * by {@link ParticipatorType}.
 */
public class AppointmentSlot extends AbstractAppointment implements Comparable<AppointmentSlot> {

    final protected UUID appointmentId;
    protected String owner;
    protected Date begin;
    protected Date end;
    protected SlotType slotType;
    protected ParticipatorType participatorType;
    protected String participatorIdentifier;

    /**
     * Create an AppointmentSlot instance. This constructor is used to <strong>create</strong> a <strong>new</strong>
     * appointment slot such that a new appointment ID will be assigned. Since {@link SlotType} is not specified via
     * this constructor's parameter, it is set to {@link SlotType#OPEN_TO_EVERYONE}, and participator information is
     * set to <code>null</code>, which is {@link AppointmentSlot#participatorType} and
     * {@link AppointmentSlot#participatorIdentifier}
     *
     * @param owner the name of the owner of this appointment slot
     * @param begin the begin time of the slot
     * @param end   the end time of the slot
     */
    public AppointmentSlot(String owner, Date begin, Date end) {
        this.owner = owner;
        this.appointmentId = UUID.randomUUID();
        this.begin = begin;
        this.end = end;
        this.slotType = SlotType.OPEN_TO_EVERYONE;
        this.participatorType = null;
        this.participatorIdentifier = null;
    }

    /**
     * Create an AppointmentSlot instance. This constructor is used to <strong>re-create</strong> an
     * <strong>existing</strong> appointment slot such that the id of the slot is known. All the fields are specified
     * via parameters of this constructor.
     *
     * @param owner                  the name of the owner of this appointment slot
     * @param id                     the id of the slot
     * @param begin                  the begin time of the slot
     * @param end                    the end time of the slot
     * @param slotType               the type of the slot described by {@link SlotType}
     * @param participatorType       the type of the participator described by {@link ParticipatorType}
     * @param participatorIdentifier the identifier of the participator, which is either a confirmation code or
     *                               username depends on <code>participatorType</code>
     */
    public AppointmentSlot(String owner, String id, Date begin, Date end, SlotType slotType,
                           ParticipatorType participatorType, String participatorIdentifier) {
        this.owner = owner;
        this.appointmentId = UUID.fromString(id);
        this.begin = begin;
        this.end = end;
        this.slotType = slotType;
        this.participatorType = participatorType;
        this.participatorIdentifier = participatorIdentifier;
    }

    /**
     * Returns the {@link Date} that this appointment begins.
     */
    public Date getBeginTime() {
        return this.begin;
    }

    /**
     * Returns the {@link Date} that this appointment ends.
     */
    public Date getEndTime() {
        return this.end;
    }

    /**
     * Returns a String describing the beginning date and time of this appointment.
     *
     * @return a string describing the beginning date and time of this appointment.
     */
    @Override
    public String getBeginTimeString() {
        return Helper.getDateString(this.begin);
    }

    /**
     * Returns a String describing the ending date and time of this appointment.
     *
     * @return a string describing the ending date and time of this appointment.
     */
    @Override
    public String getEndTimeString() {
        return Helper.getDateString(this.end);
    }

    /**
     * Returns a description of this appointment (for instance,
     * <code>"Have coffee with Marsha"</code>).
     *
     * @return a string of the description of the appointment
     */
    @Override
    public String getDescription() {
        return "";
    }

    /**
     * Returns a string for appointment Id.
     *
     * @return a string for appointment Id
     */
    public String getId() {
        return this.appointmentId.toString();
    }

    /**
     * Returns the type of the slot
     *
     * @return a {@link SlotType} instance
     */
    public SlotType getSlotType() {
        return slotType;
    }

    /**
     * Set the type of the slot, this method is usually invoked when the appointment slot is booked
     *
     * @param slotType the type of the slot to set
     */
    public void setSlotType(SlotType slotType) {
        this.slotType = slotType;
    }

    /**
     * Returns the name of the owner
     *
     * @return the name of the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns the type of the participator
     *
     * @return the type of the participator, could be <code>null</code> if not specified
     */
    public ParticipatorType getParticipatorType() {
        return participatorType;
    }

    /**
     * Set the type of participator
     *
     * @param participatorType the type of participator to set
     */
    public void setParticipatorType(ParticipatorType participatorType) {
        this.participatorType = participatorType;
    }

    /**
     * Returns the participator identifier
     *
     * @return the participator identifier
     */
    public String getParticipatorIdentifier() {
        return participatorIdentifier;
    }

    /**
     * Set the participator's identifier
     *
     * @param participatorIdentifier the participator identifier
     */
    public void setParticipatorIdentifier(String participatorIdentifier) {
        this.participatorIdentifier = participatorIdentifier;
    }

    /**
     * If the begin time is same, then compare end time according to the natural order of {@link Date}.
     *
     * @param other the <code>AppointmentSlot</code> instance to compare
     * @return integer represent the natural order
     */
    @Override
    public int compareTo(AppointmentSlot other) {
        if (this.begin.equals(other.begin)) {
            return this.end.compareTo(other.end);
        }
        return this.begin.compareTo(other.begin);
    }

    /**
     * Determine if the invoking instance has same content of <code>o</code> and type.
     *
     * @param o the <code>Object</code> to compare
     * @return <code>true</code> if <code>o</code> has same begin and end time, also it is an instance of
     * AppointmentSlot
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AppointmentSlot)) {
            return false;
        }
        AppointmentSlot other = (AppointmentSlot) o;
        // reference to {@link AppointmentSlot#compareTo} two slots are same if and only
        // if lower bounds and upper bounds are both same
        return this.end.equals(other.end) && this.begin.equals(other.begin);
    }

    public enum ParticipatorType {
        REGISTERED,
        UNREGISTERED
    }

    public enum SlotType {
        OWNER_SELF_ADDED,
        PARTICIPATOR_BOOKED,
        OPEN_TO_EVERYONE
    }

}
