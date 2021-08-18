package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.util.UUID;

/**
 * Appointment is the class that store appointment information for a certain
 * appointment, which includes a description, begin date and time, end date and
 * time. The begin date and time, and end date and time can be any string, it
 * leaves the client program to specify a typical format for its uses.
 */
public class Appointment extends AppointmentSlot {

    protected String description;

    /**
     * Create an Appointment instance. This constructor is used to <strong>create</strong> a <strong>new</strong>
     * appointment such that a new appointment ID will be assigned. This constructor will assign
     * {@link AppointmentSlot#slotType} to {@link SlotType#OWNER_SELF_ADDED}, and participator type and identifier
     * will be set to <code>null</code>.
     *
     * @param owner       the name of the appointment owner
     * @param begin       the begin time of the appointment
     * @param end         the end time of the appointment
     * @param description a description to the appointment
     */
    public Appointment(String owner, Date begin, Date end, String description) {
        super(owner, UUID.randomUUID().toString(), begin, end, SlotType.OWNER_SELF_ADDED, null, null);
        this.description = description;
    }

    /**
     * Create an Appointment instance. This constructor is used to <strong>re-create</strong> an
     * <strong>existing</strong> appointment, this requires that the id of the appointment must be known. This
     * constructor will assign {@link AppointmentSlot#slotType} to {@link SlotType#OWNER_SELF_ADDED}, and participator
     * type and identifier will be set to <code>null</code>.
     *
     * @param owner         the name of the appointment owner
     * @param appointmentId the id of the appointment
     * @param begin         the begin time of the appointment
     * @param end           the end time of the appointment
     * @param description   a description to the appointment
     */
    public Appointment(String owner, String appointmentId, Date begin, Date end, String description) {
        super(owner, appointmentId, begin, end, SlotType.OWNER_SELF_ADDED, null, null);
        this.description = description;
    }

    /**
     * Create an Appointment instance. This constructor is used to <strong>re-create</strong> and
     * <strong>existing</strong> appointment
     * such that the all the fields are specified via the parameters of this constructor.
     *
     * @param owner                  the name of the appointment owner
     * @param appointmentId          the id of the appointment
     * @param begin                  the begin time of the appointment
     * @param end                    the end time of the appointment
     * @param slotType               the type of the appointment slot indicated by {@link SlotType}
     * @param participatorType       the type of the participator indicated by {@link ParticipatorType}
     * @param participatorIdentifier the identifier of participator, either username or a confirmation code depends
     *                               on {@link ParticipatorType}; or null if no participator
     * @param description            a description to the appointment
     */
    public Appointment(String owner, String appointmentId, Date begin, Date end, SlotType slotType,
                       ParticipatorType participatorType, String participatorIdentifier, String description) {
        super(owner, appointmentId, begin, end, slotType, participatorType, participatorIdentifier);
        this.description = description;
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
     * Return an instance of {@link AppointmentSlot} describes invoking appointment's slot.
     *
     * @return a new instance of {@link AppointmentSlot}
     */
    public AppointmentSlot getAppointmentSlot() {
        return new AppointmentSlot(owner, this.appointmentId.toString(), this.begin, this.end, this.slotType,
                this.participatorType, this.participatorIdentifier);
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
    public int compareTo(AppointmentSlot appt) {
        return super.compareTo(appt) != 0 ? super.compareTo(appt) : this.description.compareTo(appt.getDescription());
    }

}
