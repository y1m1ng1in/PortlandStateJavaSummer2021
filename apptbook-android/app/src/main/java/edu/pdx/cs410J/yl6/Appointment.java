package edu.pdx.cs410J.yl6;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.UUID;

import edu.pdx.cs410J.AbstractAppointment;

/**
 * Appointment is the class that store appointment information for a certain
 * appointment, which includes a description, begin date and time, end date and
 * time. The begin date and time, and end date and time can be any string, it
 * leaves the client program to specify a typical format for its uses.
 */
@Entity(tableName = "appointments")
@TypeConverters(Converters.class)
public class Appointment extends AbstractAppointment implements Comparable<Appointment> {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "appointment_id")
    private final UUID appointmentId;

    @ColumnInfo(name = "owner")
    private final String owner;

    @ColumnInfo(name = "begin")
    private final Date begin;

    @ColumnInfo(name = "end")
    private final Date end;

    @ColumnInfo(name = "slot_type")
    private final SlotType slotType;

    @ColumnInfo(name = "participator_type")
    private final ParticipatorType participatorType;

    @ColumnInfo(name = "participator_identifier")
    private final String participatorIdentifier;

    @ColumnInfo(name = "description")
    private final String description;

    @Ignore
    static final String dateStringPattern = "M/d/yyyy h:m a";

    public Appointment(UUID appointmentId, String owner, Date begin, Date end, SlotType slotType,
                       ParticipatorType participatorType, String participatorIdentifier, String description) {
        this.appointmentId = appointmentId;
        this.owner = owner;
        this.begin = begin;
        this.end = end;
        this.slotType = slotType;
        this.participatorType = participatorType;
        this.participatorIdentifier = participatorIdentifier;
        this.description = description;
    }

    @Ignore
    public Appointment(String owner, Date begin, Date end, String description) {
        this.appointmentId = null;
        this.owner = owner;
        this.begin = begin;
        this.end = end;
        this.slotType = SlotType.OWNER_SELF_ADDED;
        this.participatorType = null;
        this.participatorIdentifier = null;
        this.description = description;
    }

    /**
     * Returns a String describing the beginning date and time of this appointment.
     *
     * @return a string describing the beginning date and time of this appointment.
     */
    @Override
    public String getBeginTimeString() {
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(dateStringPattern, Locale.US);
        return outputDateFormat.format(this.begin);
    }

    /**
     * Returns a String describing the ending date and time of this appointment.
     *
     * @return a string describing the ending date and time of this appointment.
     */
    @Override
    public String getEndTimeString() {
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(dateStringPattern, Locale.US);
        return outputDateFormat.format(this.end);
    }

    /**
     * Returns a description of this appointment (for instance,
     * <code>"Have coffee with Marsha"</code>).
     *
     * @return a string of the description of the appointment
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the {@link Date} that this appointment begins.
     */
    public Date getBegin() {
        return this.begin;
    }

    /**
     * Returns the {@link Date} that this appointment ends.
     */
    public Date getEnd() {
        return this.end;
    }

    /**
     * Returns the name of the owner
     */
    public String getOwner() {
        return this.owner;
    }

    public UUID getAppointmentId() {
        return this.appointmentId;
    }

    public SlotType getSlotType() {
        return this.slotType;
    }

    public ParticipatorType getParticipatorType() {
        return this.participatorType;
    }

    public String getParticipatorIdentifier() {
        return this.participatorIdentifier;
    }

    /**
     * Compares invoking appointment with appointment <code>appt</code> passed in.
     * If both begin time and end time are same between two appointments, then two
     * appointments are ordered by description lexicographically. Otherwise, if
     * begin time between two are same, then ordered by end time; otherwise, ordered
     * by their begin time.
     *
     * @param appt the <code>Appointment</code> instance that is to be compared with
     *             invoking appointment.
     * @return 1 if invoking appointment is ordered after parameter; 0 if two
     * appointments are same (which means both begin, end time, and
     * description are same) -1 if invoking appointment is ordered before
     * parameter.
     */
    @Override
    public int compareTo(Appointment appt) {
        if (this.begin.equals(appt.begin) && this.end.equals(appt.end)) {
            return this.description.compareTo(appt.description);
        }
        if (this.begin.equals(appt.begin)) {
            return this.end.compareTo(appt.end);
        }
        return this.begin.compareTo(appt.begin);
    }

    public static class AppointmentBuilder {

        public static final String DESCRIPTION_SHOULD_NOT_BE_EMPTY = "Field description should not be empty";
        public static final String OWNER_SHOULD_NOT_BE_EMPTY = "Field owner should not be empty";
        public static final String BEGIN_SHOULD_NOT_LATER_THAN_END =
                "Begin time is not early than end time of appointment, begin at %s, but end at %s";

        private String errorMessage;
        private String owner;
        private Date begin;
        private Date end;
        private String description;

        public Appointment build() {
            return new Appointment(this.owner, this.begin, this.end, this.description);
        }

        public Appointment buildAppointment(String owner, String begin, String end, String description) {
            if (!setOwner(owner)) {
                return null;
            }
            if (!setDescription(description)) {
                return null;
            }
            if (!setBeginTime(begin)) {
                return null;
            }
            if (!setEndTime(end)) {
                return null;
            }
            return new Appointment(this.owner, this.begin, this.end, this.description);
        }

        public boolean setOwner(String owner) {
            if (!validateStringIsNonempty(owner, "owner")) {
                return false;
            }
            this.owner = owner;
            return true;
        }

        public boolean setDescription(String description) {
            if (!validateStringIsNonempty(description, "description")) {
                return false;
            }
            this.description = description;
            return true;
        }

        public boolean setBeginTime(String beginTime) {
            if (!validateStringIsNonempty(beginTime, "begin time")) {
                return false;
            }
            DateFormat df = new SimpleDateFormat(dateStringPattern);
            df.setLenient(false);
            try {
                this.begin = df.parse(beginTime);
            } catch (ParseException e) {
                this.errorMessage = e.getMessage();
                return false;
            }
            return true;
        }

        public boolean setEndTime(String endTime) {
            if (!validateStringIsNonempty(endTime, "begin time")) {
                return false;
            }
            DateFormat df = new SimpleDateFormat(dateStringPattern);
            df.setLenient(false);
            Date end;
            try {
                end = df.parse(endTime);
            } catch (ParseException e) {
                this.errorMessage = e.getMessage();
                return false;
            }
            if (!begin.before(end)) {
                this.errorMessage = "Begin time is not early than end time of appointment";
                return false;
            }
            this.end = end;
            return true;
        }

        private boolean validateStringIsNonempty(String s, String fieldName) {
            String trimmedString = s.trim();
            if (trimmedString.equals("")) {
                this.errorMessage = String.format("Field %s should not be empty", fieldName) ;
                return false;
            }
            return true;
        }

        /**
         * Get error message reported by the last call of <code>isValid</code>
         *
         * @return error message reported by the last call of <code>isValid</code>
         */
        public String getErrorMessage() {
            return errorMessage;
        }
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