package edu.pdx.cs410J.yl6.database;

import edu.pdx.cs410J.yl6.*;
import edu.pdx.cs410J.yl6.database.plaintextoperator.*;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

/**
 * PlainTextFileDatabase is the class that realizes the interface {@link AppointmentBookStorage} such that the
 * server can store data using a collection of plain texts.
 */
public class PlainTextFileDatabase implements AppointmentBookStorage {

    static final private String userdb = "db_user.txt";
    private static volatile AppointmentBookStorage INSTANCE;
    private final File dir;
    private final AppointmentValidator validator = new AppointmentValidator("M/d/yyyy h:m a");

    /**
     * Create a PlainTextFileDatabase instance by specifying that all the plain texts are stored under directory
     * <code>dir</code>
     *
     * @param dir the directory for plain texts to be stored
     */
    private PlainTextFileDatabase(File dir) {
        this.dir = dir;
        if (!this.dir.exists() || !this.dir.isDirectory()) {
            this.dir.mkdirs();
        }
    }

    /**
     * Get the <strong>singleton</strong> instance of PlainTextFileDatabase.
     *
     * @param dir specify that all the plain texts are stored under directory <code>dir</code>
     * @return the <strong>singleton</strong> instance of PlainTextFileDatabase
     */
    public static AppointmentBookStorage getDatabase(File dir) {
        if (INSTANCE == null) {
            synchronized (PlainTextFileDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PlainTextFileDatabase(dir);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Read all the appointments with <code>owner</code>, and create an appointment
     * book that contains all the read appointments with owner name <code>owner</code>.
     *
     * @param owner the name of the owner of appointments
     * @return an {@link AppointmentBook} that contains all the read appointments if
     * there exists at least one appointment with <code>owner</code>; <code>null</code> otherwise.
     * @throws StorageException If file is malformed such that it can't be parsed.
     */
    @Override
    public AppointmentBook<Appointment> getAllAppointmentsByOwner(String owner) throws StorageException {
        // appointment id -> appointment slot
        Map<String, AppointmentSlot> idToSlot = getAllAppointmentSlotsByOwner(owner);
        // appointment id -> appointment description
        Map<String, String> idToDescription = getAllAppointmentIdToDescriptionsByOwner(owner);

        AppointmentBook<Appointment> appointmentBook = new AppointmentBook<>(owner);

        for (String id : idToSlot.keySet()) {
            if (idToDescription.containsKey(id)) {
                // appointment has a description, then it is not a bookable one
                AppointmentSlot slot = idToSlot.get(id);
                Appointment appointment = new Appointment(slot.getOwner(), id, slot.getBeginTime(), slot.getEndTime(),
                        slot.getSlotType(), slot.getParticipatorType(), slot.getParticipatorIdentifier(),
                        idToDescription.get(id));
                appointmentBook.addAppointment(appointment);
            }
        }

        if (appointmentBook.getAppointments().isEmpty()) {
            return null;
        }
        return appointmentBook;
    }

    /**
     * Read all the appointment slots that are bookable owned by <code>owner</code>.
     *
     * @param owner the appointment owner
     * @return an <code>AppointmentBook</code> that contains all the bookable appointment slots if there exists at
     * least one appointment slot; <code>null</code> otherwise.
     * @throws StorageException If file is malformed such that it can't be parsed.
     */
    @Override
    public AppointmentBook<AppointmentSlot> getAllBookableAppointmentSlotsByOwner(String owner) throws StorageException {
        // appointment id -> appointment slot
        Map<String, AppointmentSlot> idToSlot = getAllAppointmentSlotsByOwner(owner);
        // appointment id -> appointment description
        Map<String, String> idToDescription = getAllAppointmentIdToDescriptionsByOwner(owner);

        AppointmentBook<AppointmentSlot> appointmentBook = new AppointmentBook<>(owner);

        for (String id : idToSlot.keySet()) {
            if (!idToDescription.containsKey(id)) {
                // appointment has a description, then it is not a bookable one
                AppointmentSlot slot = idToSlot.get(id);
                appointmentBook.addAppointment(slot);
            }
        }

        if (appointmentBook.getAppointments().isEmpty()) {
            return null;
        }
        return appointmentBook;
    }

    /**
     * Read all appointments with <code>owner</code> that begins between
     * <code>from</code> and <code>to</code>. If no appointment found that falls
     * into this interval, then this method returns <code>null</code>
     *
     * @param owner the owner name
     * @param from  the lowerbound of <code>Date</code> instance
     * @param to    the upperbound of <code>Date</code> instance
     * @return an {@link AppointmentBook} that contains all the appointments
     * satisfied; <code>null</code> if nothing found.
     * @throws StorageException If file cannot found, or file is malformed such
     *                          that it can't be parsed.
     */
    @Override
    public AppointmentBook<Appointment> getAppointmentsByOwnerWithBeginInterval(String owner, Date from, Date to)
            throws StorageException {
        AppointmentBook<Appointment> parsed = getAllAppointmentsByOwner(owner);
        if (parsed == null) {
            return null;
        }
        AppointmentBook<Appointment> satisifed = new AppointmentBook<>(owner);
        for (Appointment appt : parsed.getAppointments()) {
            Date beginAt = appt.getBeginTime();
            if (beginAt.after(from) && beginAt.before(to)) {
                satisifed.addAppointment(appt);
            }
        }
        if (satisifed.getAppointments().isEmpty()) {
            return null;
        }
        return satisifed;
    }

    /**
     * Store {@link Appointment} instance owned by <code>owner</code> to storage.
     *
     * @param owner       the name of <code>appointment</code> owner
     * @param appointment the appointment to store
     * @throws StorageException If an input or output exception occurs during performing on file
     */
    @Override
    public boolean insertAppointmentWithOwner(String owner, Appointment appointment) throws StorageException {
        // appointment id -> appointment slot
        Map<String, AppointmentSlot> idToSlot = getAllAppointmentSlotsByOwner(owner);
        // appointment id -> appointment description
        Map<String, String> idToDescription = getAllAppointmentIdToDescriptionsByOwner(owner);

        if (idToSlot.containsKey(appointment.getId())) {
            throw new StorageException("appointment id already exists in file that maps appointment id to slot");
        }
        if (idToDescription.containsKey(appointment.getId())) {
            throw new StorageException("appointment id already exists in file that maps appointment id to description");
        }

        // check if appointment to add is compatible with all existing appointments
        if (!verifySlotIsCompatibleWithAll(owner, appointment, idToSlot)) return false;

        appendSlot(owner, appointment);
        appendDescription(owner, appointment);
        return true;
    }

    /**
     * Store {@link AppointmentSlot} instance owned by <code>owner</code> to storage.
     *
     * @param owner the name of the owner
     * @param slot  an {@link AppointmentSlot} the is bookable
     * @return <code>true</code> if there is no conflict with existing appointment slots of <code>owner</code>;
     * <code>false</code> otherwise.
     * @throws StorageException If an input or output exception occurs during performing on file
     */
    @Override
    public boolean insertBookableAppointmentSlot(String owner, AppointmentSlot slot) throws StorageException {
        // appointment id -> appointment slot
        Map<String, AppointmentSlot> idToslot = getAllAppointmentSlotsByOwner(owner);

        if (idToslot.containsKey(slot.getId())) {
            throw new StorageException("appointment id already exists in file that maps appointment id to slot");
        }

        // check if slot to add is compatible with all existing appointments
        if (!verifySlotIsCompatibleWithAll(owner, slot, idToslot)) return false;

        appendSlot(owner, slot);
        return true;
    }

    /**
     * Book a bookable appointment slot.
     *
     * @param owner         the name of the owner
     * @param appointment   an {@link Appointment} instance to be booked
     * @param authenticated indicates if the booker is authenticated
     * @return {@link AppointmentBookStorage#NOT_BOOKABLE} if the appointment slot cannot be booked;
     * {@link AppointmentBookStorage#CONFLICT_WITH_EXISTING_APPOINTMENT} if the appointment slot conflicts with
     * existing appointment of authenticated user; otherwise, {@link AppointmentBookStorage#BOOK_SUCCESS}
     * @throws StorageException If an input or output exception occurs during performing on file
     */
    @Override
    public int bookAppointment(String owner, Appointment appointment, boolean authenticated) throws StorageException {
        // appointment id -> appointment description
        Map<String, String> idToDescription = getAllAppointmentIdToDescriptionsByOwner(owner);
        // appointment id -> appointment slot
        Map<String, AppointmentSlot> idToslot = getAllAppointmentSlotsByOwner(owner);

        if (idToDescription.containsKey(appointment.getId())) {
            throw new StorageException("appointment id already exists in file that maps appointment id to description");
        }
        if (!idToslot.containsKey(appointment.getId())) {
            throw new StorageException("appointment id does not exist in file that maps appointment id to slot");
        }

        if (!verifySlotIsBookable(appointment, idToDescription, idToslot)) return NOT_BOOKABLE;

        // check if slot to add is compatible with all existing appointments when user is logged in
        if (authenticated && !insertAppointmentWithOwner(appointment.getParticipatorIdentifier(), appointment)) {
            return CONFLICT_WITH_EXISTING_APPOINTMENT;
        }

        idToslot.put(appointment.getId(), appointment.getAppointmentSlot());

        updateSlot(owner, idToslot);
        appendDescription(owner, appointment);

        return BOOK_SUCCESS;
    }

    /**
     * Verify that given <code>appointment</code> is time-compatible with all existing slots stored in
     * <code>idToslot</code>.
     *
     * @param owner       the name of the owner
     * @param appointment the appointment to check against all appointments of <code>owner</code>
     * @param idToslot    a {@link Map} that maps each appointment id to {@link AppointmentSlot} stores all appointment
     *                    slots of <code>owner</code>
     * @return <code>true</code> if <code>appointment</code> does not conflict with all appointment slots in
     * <code>idToslot</code>, <code>false</code> otherwise.
     */
    private boolean verifySlotIsCompatibleWithAll(String owner, AppointmentSlot appointment,
                                                  Map<String, AppointmentSlot> idToslot) {
        AppointmentBook<AppointmentSlot> existingSlots = new AppointmentBook<>(owner);
        for (AppointmentSlot existingSlot : idToslot.values()) {
            existingSlots.addAppointment(existingSlot);
        }
        return !existingSlots.contains(appointment);
    }

    /**
     * Verify that given <code>appointment</code> its appointment slot is bookable. It will check the id of
     * <code>appointment</code> exists, the begin time and end time matches the ones recorded associated with id.
     * Any of above does not match, then it is not bookable.
     *
     * @param appointment     the appointment to verify
     * @param idToDescription a {@link Map} that maps each appointment id to appointment descriptions stores all
     *                        appointment descriptions of <code>owner</code>
     * @param idToSlot        a {@link Map} that maps each appointment id to {@link AppointmentSlot} stores all
     *                        appointment slots of <code>owner</code>
     * @return <code>true</code> if <code>appointment</code> is bookable, <code>false</code> otherwise.
     */
    private boolean verifySlotIsBookable(Appointment appointment, Map<String, String> idToDescription, Map<String,
            AppointmentSlot> idToSlot) {
        AppointmentSlot toCompare = idToSlot.get(appointment.getId());

        if (!idToSlot.containsKey(appointment.getId()) || idToDescription.containsKey(appointment.getId())) {
            return false;
        }
        return toCompare.getBeginTime().equals(appointment.getBeginTime())
                && toCompare.getEndTime().equals(appointment.getEndTime());
    }

    /**
     * Append an {@link AppointmentSlot} to file storage.
     *
     * @param owner the name of the owner
     * @param slot  the slot to append
     * @throws StorageException If an input or output exception occurs during performing on file
     */
    private void appendSlot(String owner, AppointmentSlot slot) throws StorageException {
        File slots = new File(this.dir, owner + "_slots.txt");
        try (Writer slotWrite = new FileWriter(slots, true)) {
            AppointmentTableEntryDumper.AppointmentSlotTableEntryDumper slotDumper =
                    new AppointmentTableEntryDumper.AppointmentSlotTableEntryDumper(slotWrite);
            slotDumper.dump(slot);
        } catch (IOException e) {
            throw new StorageException("While writing new appointment slot to storage, " + e.getMessage());
        }
    }

    /**
     * Update an {@link AppointmentSlot} that stored in the file storage.
     *
     * @param owner    the name of the owner
     * @param idToSlot all the slots of owner to overwrite existing file.
     * @throws StorageException If an input or output exception occurs during performing on file
     */
    private void updateSlot(String owner, Map<String, AppointmentSlot> idToSlot) throws StorageException {
        File slots = new File(this.dir, owner + "_slots.txt");
        try (Writer slotWriter = new FileWriter(slots)) {
            AppointmentTableEntryDumper.AppointmentSlotTableEntryDumper slotTableEntryDumper =
                    new AppointmentTableEntryDumper.AppointmentSlotTableEntryDumper(slotWriter);
            for (AppointmentSlot slot : idToSlot.values()) {
                slotTableEntryDumper.dump(slot);
            }
        } catch (IOException e) {
            throw new StorageException("While writing new appointment slot to storage, " + e.getMessage());
        }
    }

    /**
     * Append a appointment id and description pair to file storage.
     *
     * @param owner       the name of the owner
     * @param appointment the appointment that contains id and description to append
     * @throws StorageException If an input or output exception occurs during performing on file
     */
    private void appendDescription(String owner, Appointment appointment) throws StorageException {
        File descriptions = new File(this.dir, owner + "_descriptions.txt");
        try (Writer descriptionWriter = new FileWriter(descriptions, true)) {
            AppointmentTableEntryDumper.AppointmentDescriptionTableEntryDumper descriptionDumper =
                    new AppointmentTableEntryDumper.AppointmentDescriptionTableEntryDumper(descriptionWriter);
            descriptionDumper.dump(appointment);
        } catch (IOException e) {
            throw new StorageException("While writing new appointment description to storage, " + e.getMessage());
        }
    }

    /**
     * Insert a {@link User} instance to the storage
     *
     * @param user an <code>User</code> instance
     * @return {@link AppointmentBookStorage#USERNAME_CONFLICT} or {@link AppointmentBookStorage#EMAIL_CONFLICT} if
     * username or email address has been registered; {@link AppointmentBookStorage#REGISTER_USER_SUCCESS} otherwise
     * @throws StorageException If any error occurs during read/write with storage
     */
    @Override
    public int insertUser(User user) throws StorageException {
        if (getFirstOccurrenceUserSatisfied(user1 -> user1.getUsername().equals(user.getUsername())) != null) {
            return USERNAME_CONFLICT;
        } else if (getFirstOccurrenceUserSatisfied(user1 -> user1.getEmail().equals(user.getEmail())) != null) {
            return EMAIL_CONFLICT;
        }
        try (FileWriter fw = new FileWriter(new File(this.dir, userdb), true); BufferedWriter bw =
                new BufferedWriter(fw)) {
            UserTableEntryDumper.UserProfilerTableEntryDumper dumper =
                    new UserTableEntryDumper.UserProfilerTableEntryDumper(bw);
            dumper.dump(user);
            bw.flush();
        } catch (IOException e) {
            throw new StorageException("While storing user to storage, " + e.getMessage());
        }
        return REGISTER_USER_SUCCESS;
    }

    /**
     * Get a {@link User} instance by its username
     *
     * @param username the name of user
     * @return a {@link User} instance
     * @throws StorageException If any read/write with file occurs
     */
    @Override
    public User getUserByUsername(String username) throws StorageException {
        return getFirstOccurrenceUserSatisfied(user -> user.getUsername().equals(username));
    }

    /**
     * Retrieve a {@link User} instance in the file storage such that it satisfies <code>condition</code> by invoking
     * <code>condition</code> and it returns <code>true</code>.
     *
     * @param condition a function that takes a {@link User} instance as parameter and returns a boolean
     * @return a {@link User} instance if found, <code>null</code> otherwise
     * @throws StorageException If an input or output exception occurs during performing on file
     */
    private User getFirstOccurrenceUserSatisfied(Function<User, Boolean> condition) throws StorageException {
        File f = new File(this.dir, userdb);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (Reader reader = new BufferedReader(new FileReader(f))) {
            PlainTextIterator<UserTableEntryParser.UserProfilerTableEntryParser, User> iterator =
                    new PlainTextFileDatabase.PlainTextIterator<>(
                            new UserTableEntryParser.UserProfilerTableEntryParser(reader));
            while (iterator.hasNext()) {
                User user = iterator.next();
                if (condition.apply(user)) {
                    return user;
                }
            }
        } catch (IOException e) {
            throw new StorageException("While retrieving user to storage, " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieve all the appointment slots of <code>owner</code>, and construct a {@link Map} that maps appointment id
     * to its {@link AppointmentSlot} instance which makes further query by id easier.
     *
     * @param owner the name of the owner
     * @return a {@link Map} that maps appointment id to its {@link AppointmentSlot} instance
     * @throws StorageException If an input or output exception occurs during performing on file
     */
    private Map<String, AppointmentSlot> getAllAppointmentSlotsByOwner(String owner) throws StorageException {
        Map<String, AppointmentSlot> mapping = new HashMap<>();
        File slots = new File(this.dir, owner + "_slots.txt");
        if (!slots.exists()) {
            return mapping;
        }
        try (Reader reader = new FileReader(slots)) {
            PlainTextIterator<AppointmentTableEntryParser.AppointmentSlotTableEntryParser, AppointmentSlot> iterator =
                    new PlainTextIterator<>(
                            new AppointmentTableEntryParser.AppointmentSlotTableEntryParser(reader));
            while (iterator.hasNext()) {
                AppointmentSlot slot = iterator.next();
                mapping.put(slot.getId(), slot);
            }
            return mapping;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new StorageException("While closing reader in storage, " + e.getMessage());
        }
    }

    /**
     * Retrieve all the appointment id and its corresponding appointment description belong to <code>owner</code>,and
     * construct a {@link Map} that maps appointment id to its description which makes further query by id easier.
     *
     * @param owner the name of the owner
     * @return a {@link Map} that maps appointment id to its description
     * @throws StorageException If an input or output exception occurs during performing on file
     */
    private Map<String, String> getAllAppointmentIdToDescriptionsByOwner(String owner) throws StorageException {
        Map<String, String> mapping = new HashMap<>();
        File descriptions = new File(this.dir, owner + "_descriptions.txt");
        if (!descriptions.exists()) {
            return mapping;
        }
        try (Reader reader = new FileReader(descriptions)) {
            PlainTextRowIterator<AppointmentTableEntryParser.AppointmentDescriptionTableEntryParser> iterator =
                    new PlainTextRowIterator<>(
                            new AppointmentTableEntryParser.AppointmentDescriptionTableEntryParser(reader));
            while (iterator.hasNext()) {
                String[] map = iterator.next();
                mapping.put(map[0], map[1]);
            }
            return mapping;
        } catch (IOException e) {
            throw new StorageException("While closing reader in storage, " + e.getMessage());
        }
    }

    /**
     * A class that works as an iterator that "traverses" a file by invoking
     * {@link TableEntryParser#parse}. Thus, it avoids reading the whole file into the memory.
     */
    public class PlainTextIterator<T extends TableEntryParser<E>, E> implements Iterator<E> {

        private final TableEntryParser<E> entryParser;
        private E temp;

        /**
         * Create a PlainTextIterator instance, moving the "cursor" of {@link Reader} to
         * the place right after the last character of the meta information, so the next
         * read should be the first character of the first entry
         *
         * @param entryParser the parser parses an individual <code>E</code>
         */
        public PlainTextIterator(TableEntryParser<E> entryParser) {
            this.entryParser = entryParser;
        }

        /**
         * Store the next entry that should be returned by <code>next</code>, if the
         * next entry is null, then it shows that we don't have a "next".
         *
         * @return <code>true</code> if parsed a <code>E</code> that is not
         * <code>null</code>; <code>false</code> otherwise
         */
        @Override
        public boolean hasNext() {
            try {
                this.temp = this.entryParser.parse();
            } catch (Exception e) {
                return false;
            }
            return this.temp != null;
        }

        /**
         * Return an instance of <code>E</code> get parsed from the last call to
         * <code>hasNext</code>
         *
         * @return an instance of <code>E</code> get parsed from the last call to
         * <code>hasNext</code>
         */
        @Override
        public E next() {
            return this.temp;
        }
    }

    /**
     * A class that works as an iterator that "traverses" a file by invoking
     * {@link TableEntryParser#getRow}. Thus, it avoids reading the whole file into the
     * memory.
     */
    public class PlainTextRowIterator<T extends TableEntryParser<?>> implements Iterator<String[]> {

        private final TableEntryParser<?> entryParser;
        private String[] temp;

        /**
         * Create a PlainTextIterator instance, moving the "cursor" of {@link Reader} to
         * the place right after the last character of the meta information, so the next
         * read should be the first character of the first entry
         *
         * @param entryParser the parser parses an individual <code>E</code>
         */
        public PlainTextRowIterator(TableEntryParser<?> entryParser) {
            this.entryParser = entryParser;
        }

        /**
         * Store the next entry that should be returned by <code>next</code>, if the
         * next entry is null, then it shows that we don't have a "next".
         *
         * @return <code>true</code> if parsed a <code>E</code> that is not
         * <code>null</code>; <code>false</code> otherwise
         */
        @Override
        public boolean hasNext() {
            try {
                this.temp = this.entryParser.getRow();
            } catch (Exception e) {
                return false;
            }
            return this.temp != null;
        }

        /**
         * Return an instance of <code>E</code> get parsed from the last call to
         * <code>hasNext</code>
         *
         * @return an instance of <code>E</code> get parsed from the last call to
         * <code>hasNext</code>
         */
        @Override
        public String[] next() {
            return this.temp;
        }
    }
}
