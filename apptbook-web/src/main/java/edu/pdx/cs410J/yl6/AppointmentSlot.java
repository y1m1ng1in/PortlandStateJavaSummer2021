package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.AbstractAppointment;

import java.util.Date;
import java.util.UUID;

public class AppointmentSlot extends AbstractAppointment implements Comparable<AppointmentSlot> {

    final protected UUID appointmentId;
    protected String owner;
    protected Date begin;
    protected Date end;
    protected SlotType slotType;
    protected ParticipatorType participatorType;
    protected String participatorIdentifier;

    public AppointmentSlot(String owner, Date begin, Date end) {
        this.owner = owner;
        this.appointmentId = UUID.randomUUID();
        this.begin = begin;
        this.end = end;
        this.slotType = SlotType.OPEN_TO_EVERYONE;
        this.participatorType = null;
        this.participatorIdentifier = null;
    }

    public AppointmentSlot(String owner, String id, Date begin, Date end, SlotType slotType,
                           ParticipatorType participatorType,
                           String participatorIdentifier) {
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

    public SlotType getSlotType() {
        return slotType;
    }

    public String getOwner() {
        return owner;
    }

    public void setSlotType(SlotType slotType) {
        this.slotType = slotType;
    }

    public ParticipatorType getParticipatorType() {
        return participatorType;
    }

    public void setParticipatorType(ParticipatorType participatorType) {
        this.participatorType = participatorType;
    }

    public String getParticipatorIdentifier() {
        return participatorIdentifier;
    }

    public void setParticipatorIdentifier(String participatorIdentifier) {
        this.participatorIdentifier = participatorIdentifier;
    }

    @Override
    public int compareTo(AppointmentSlot other) {
        if (this.begin.equals(other.begin)) {
            return this.end.compareTo(other.end);
        }
        return this.begin.compareTo(other.begin);
    }

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
