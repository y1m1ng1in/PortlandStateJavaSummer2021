package edu.pdx.cs410J.yl6;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

import edu.pdx.cs410J.AbstractAppointment;

/**
 * Appointment is the class that store appointment information for a certain
 * appointment, which includes a description, begin date and time, end date and
 * time. The begin date and time, and end date and time can be any string, it
 * leaves the client program to specify a typical format for its uses.
 */
@Entity(tableName = "appointments")
@TypeConverters(Converters.class)
public class Appointment extends AbstractAppointment
        implements Comparable<Appointment> {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "owner")
    private final String owner;

    @ColumnInfo(name = "begin")
    private final Date begin;

    @ColumnInfo(name = "end")
    private final Date end;

    @ColumnInfo(name = "description")
    private final String description;

    @Ignore
    private final String beginString;

    @Ignore
    private final String endString;

    @Ignore
    static final String dateStringPattern = "M/d/yyyy h:m a";

    /**
     * Create an appointment instance, where {@link SimpleDateFormat} is used to
     * parse string <code>begin</code> and <code>end</code> as begin and end time of
     * the appointment.
     *
     * @param owner       the name of the owner
     * @param begin       a string that is parseable by
     *                    <code>SimpleDateFormat</code> in pattern
     *                    <code>"M/d/yyyy h:m a"</code> and before <code>end</code>
     * @param end         a string that is parsable by <code>SimpleDateFormat</code>
     *                    in pattern <code>"M/d/yyyy h:m a"</code> and after
     *                    <code>before</code>
     * @param description a nonempty string that describes the appointment
     * @throws ParseException the <code>begin</code> and <code>end</code> cannot be
     *                        parsed by <code>SimpleDateFormat</code> successfully.
     */
    public Appointment(String owner, Date begin, Date end, String description) {
        this.owner = owner;
        this.begin = begin;
        this.end = end;
        this.description = description;

        SimpleDateFormat outputDateFormat = new SimpleDateFormat(dateStringPattern);
        this.beginString = outputDateFormat.format(this.begin);
        this.endString = outputDateFormat.format(this.end);
    }

    /**
     * Returns a String describing the beginning date and time of this appointment.
     *
     * @return a string describing the beginning date and time of this appointment.
     */
    @Override
    public String getBeginTimeString() {
        return this.beginString;
    }

    /**
     * Returns a String describing the ending date and time of this appointment.
     *
     * @return a string describing the ending date and time of this appointment.
     */
    @Override
    public String getEndTimeString() {
        return this.endString;
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

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
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

}