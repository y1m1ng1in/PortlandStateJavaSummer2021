package edu.pdx.cs410J.yl6.database;

import edu.pdx.cs410J.yl6.Appointment;
import edu.pdx.cs410J.yl6.AppointmentBook;
import edu.pdx.cs410J.yl6.AppointmentSlot;
import edu.pdx.cs410J.yl6.User;
import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * PostgresqlDatabase is the class that realizes the interface {@link AppointmentBookStorage} such that the
 * server can read/write data in a Postgre database server.
 */
public class PostgresqlDatabase implements AppointmentBookStorage {

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("M/d/yyyy h:m a");
    private static volatile AppointmentBookStorage INSTANCE;
    private final PoolingDataSource<PoolableConnection> dataSource;

    /**
     * Private constructor which is used to instantiate {@link PostgresqlDatabase#INSTANCE} as a singleton.
     *
     * @param url      the url of the server connection
     * @param username the login name of the database
     * @param password the password of the login database user
     */
    private PostgresqlDatabase(String url, String username, String password) {
        ConnectionFactory connectionFactory;

        if (username == null && password == null) {
            connectionFactory = new DriverManagerConnectionFactory(url);
        } else {
            connectionFactory = new DriverManagerConnectionFactory(url, username, password);
        }

        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
        dataSource = new PoolingDataSource<>(connectionPool);

        String createSlotTypeSql = String.format(
                "DO $$ BEGIN IF NOT EXISTS (select 1 from pg_type where typname = 'appointment_slot_type') THEN " +
                        "create type appointment_slot_type as enum ('%s', '%s', '%s'); END IF; END; $$",
                AppointmentSlot.SlotType.OPEN_TO_EVERYONE, AppointmentSlot.SlotType.PARTICIPATOR_BOOKED,
                AppointmentSlot.SlotType.OWNER_SELF_ADDED);

        String createParticipatorTypeSql = String.format(
                "DO $$ BEGIN IF NOT EXISTS (select 1 from pg_type where typname = 'appointment_participator_type') " +
                        "THEN create type appointment_participator_type as enum ('%s', '%s'); END IF; END; $$",
                AppointmentSlot.ParticipatorType.REGISTERED, AppointmentSlot.ParticipatorType.UNREGISTERED);

        String createAppointmentSlotTableSql = "CREATE TABLE IF NOT EXISTS appointment_slots (" +
                "id varchar(255) primary key," +
                "owner varchar(20) not null," +
                "begin_time timestamp not null," +
                "end_time timestamp not null," +
                "slot_type appointment_slot_type not null," +
                "participator_type appointment_participator_type," +
                "participator_identifier varchar(255)," +
                "FOREIGN KEY (owner) REFERENCES user_profile (username)" +
                ")";

        String createAppointmentDescriptionTableSql = "CREATE TABLE IF NOT EXISTS appointment_descriptions (" +
                "id varchar(255)," +
                "description varchar(200) not null," +
                "FOREIGN KEY (id) REFERENCES appointment_slots (id)" +
                ")";

        String createUserProfileTableSql = "CREATE TABLE IF NOT EXISTS user_profile (" +
                "id varchar(255) not null," +
                "username varchar(20) primary key," +
                "password varchar(255) not null," +
                "email varchar(255) not null," +
                "address varchar(255) not null," +
                "UNIQUE (email)," +
                "UNIQUE (id)" +
                ")";

        Connection conn = null;
        Statement stmt = null;
        try {
            System.out.println("Creating connection.");

            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            stmt = conn.createStatement();
            stmt.executeUpdate(createSlotTypeSql);
            stmt.executeUpdate(createParticipatorTypeSql);
            stmt.executeUpdate(createUserProfileTableSql);
            stmt.executeUpdate(createAppointmentSlotTableSql);
            stmt.executeUpdate(createAppointmentDescriptionTableSql);

            conn.setAutoCommit(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                conn.close();
                stmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * Get the singleton PostgresqlDatabase instance. If constructor is invoked, using default parameter
     *
     * @return singleton instance of PostgresqlDatabase
     */
    public static AppointmentBookStorage getDatabase() {
        if (INSTANCE == null) {
            synchronized (PostgresqlDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PostgresqlDatabase("jdbc:postgresql://localhost:5432/postgres", "yiminglin",
                            "971013970206Lym");
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Get the singleton PostgresqlDatabase instance. If constructor is invoked, parameters passed in will be used.
     *
     * @param url      the url of the database connection
     * @param username the login name to access the database
     * @param password the password to login
     * @return
     */
    public static AppointmentBookStorage getDatabase(String url, String username, String password) {
        if (INSTANCE == null) {
            synchronized (PostgresqlDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PostgresqlDatabase(url, username, password);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Get an appointment book that contains all appointments belong to <code>owner</code> in the storage.
     *
     * @param owner the owner name
     * @return an appointment book that contains all appointments with
     * <code>owner</code>
     * @throws StorageException If any error occurs during read/write with storage
     */
    @Override
    public AppointmentBook<Appointment> getAllAppointmentsByOwner(String owner) throws StorageException {
        String getAppointmentsStatement = "select * " +
                "from appointment_slots inner join appointment_descriptions " +
                "on appointment_slots.id = appointment_descriptions.id " +
                "where (appointment_slots.owner = ? or appointment_slots.participator_identifier = ?) " +
                "and appointment_descriptions.description is not null " +
                "and appointment_slots.slot_type <> ?";
        AppointmentBook<Appointment> result = new AppointmentBook<>(owner);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement getAppointments = conn.prepareStatement(getAppointmentsStatement)) {
            getAppointments.setString(1, owner);
            getAppointments.setString(2, owner);
            getAppointments.setObject(3, AppointmentSlot.SlotType.OPEN_TO_EVERYONE, Types.OTHER);
            ResultSet rs = getAppointments.executeQuery();
            formAppointmentQueryResults(result, rs);
        } catch (SQLException throwables) {
            throwables.printStackTrace(System.err);
            throw new StorageException(throwables.getMessage());
        }

        if (result.getAppointments().isEmpty()) {
            return null;
        }
        return result;
    }

    private void formAppointmentQueryResults(AppointmentBook<Appointment> result, ResultSet rs) throws SQLException {
        while (rs.next()) {
            String id = rs.getString(1);
            String ownerName = rs.getString(2);
            Date begin = rs.getTimestamp(3);
            Date end = rs.getTimestamp(4);
            String slotType = rs.getString(5);
            String participator = rs.getString(6);
            String participatorIdentifier = rs.getString(7);
            String description = rs.getString(9);
            Appointment appointment = new Appointment(ownerName, id, begin, end,
                    AppointmentSlot.SlotType.valueOf(slotType),
                    participator == null ? null : AppointmentSlot.ParticipatorType.valueOf(participator),
                    participatorIdentifier, description);
            result.addAppointment(appointment);
        }
    }

    private void formAppointmentSlotQueryResults(AppointmentBook<AppointmentSlot> result, ResultSet rs) throws SQLException {
        while (rs.next()) {
            String id = rs.getString(1);
            String ownerName = rs.getString(2);
            Date begin = rs.getTimestamp(3);
            Date end = rs.getTimestamp(4);
            String slotType = rs.getString(5);
            String participator = rs.getString(6);
            String participatorIdentifier = rs.getString(7);
            AppointmentSlot appointmentSlot = new AppointmentSlot(ownerName, id, begin, end,
                    AppointmentSlot.SlotType.valueOf(slotType),
                    participator == null ? null : AppointmentSlot.ParticipatorType.valueOf(participator),
                    participatorIdentifier);
            result.addAppointment(appointmentSlot);
        }
    }

    /**
     * Retrieve the storage to get a collection of appointments that begin between <code>from</code> to <code>to</code>.
     * If no appointment found that falls into this interval, then this method returns <code>null</code>
     *
     * @param owner the owner name of the appointments to be retrieved
     * @param from  the earliest begin time of appointments to be retrieved
     * @param to    the latest begin time of appointments to be retrieved
     * @return an appointment book <code>T</code> that contains all the appointments
     * whose begin time is between <code>from</code> to <code>to</code>.
     * @throws StorageException If any error occurs during read/write with storage
     */
    @Override
    public AppointmentBook<Appointment> getAppointmentsByOwnerWithBeginInterval(String owner, Date from, Date to) throws StorageException {
        String getAppointmentsStatement = "select * " +
                "from appointment_slots inner join appointment_descriptions " +
                "on appointment_slots.id = appointment_descriptions.id " +
                "where (appointment_slots.owner = ? or appointment_slots.participator_identifier = ?) " +
                "and appointment_descriptions.description is not null " +
                "and appointment_slots.slot_type <> ? and begin_time > ? and begin_time < ?";
        AppointmentBook<Appointment> result = new AppointmentBook<>(owner);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement getAppointments = conn.prepareStatement(getAppointmentsStatement)) {
            getAppointments.setString(1, owner);
            getAppointments.setString(2, owner);
//            getAppointments.setString(2, AppointmentSlot.SlotType.OPEN_TO_EVERYONE.toString());
            getAppointments.setObject(3, AppointmentSlot.SlotType.OPEN_TO_EVERYONE, Types.OTHER);
            getAppointments.setTimestamp(4, new Timestamp(from.getTime()));
            getAppointments.setTimestamp(5, new Timestamp(to.getTime()));

            ResultSet rs = getAppointments.executeQuery();
            formAppointmentQueryResults(result, rs);
        } catch (SQLException throwables) {
            throw new StorageException(throwables.getMessage());
        }

        if (result.getAppointments().isEmpty()) {
            return null;
        }
        return result;
    }

    /**
     * Insert <code>appointment</code> to the storage.
     *
     * @param owner       the owner of <code>appointment</code>
     * @param appointment the appointment to be stored persistently.
     * @throws StorageException If any error occurs during read/write with storage
     */
    @Override
    public boolean insertAppointmentWithOwner(String owner, Appointment appointment) throws StorageException {
        String compatibility = "select not exists (select 1 from appointment_slots " +
                "where (appointment_slots.owner = ? or appointment_slots.participator_identifier = ?) " +
                "and begin_time <= ? and end_time >= ?)";
        String insertIntoSlotTable = "insert into appointment_slots values (?, ?, ?, ?, ?, ?, ?)";
        String insertIntoDescriptionTable = "insert into appointment_descriptions values (?, ?)";
        boolean isCompatible;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement checkCompatibilityStmt = conn.prepareStatement(compatibility);
             PreparedStatement insertSlotStmt = conn.prepareStatement(insertIntoSlotTable);
             PreparedStatement insertDescriptionStmt = conn.prepareStatement(insertIntoDescriptionTable)) {
            conn.setAutoCommit(false);

            checkCompatibilityStmt.setString(1, owner);
            checkCompatibilityStmt.setString(2, owner);
            checkCompatibilityStmt.setTimestamp(3, new Timestamp(appointment.getEndTime().getTime()));
            checkCompatibilityStmt.setTimestamp(4, new Timestamp(appointment.getBeginTime().getTime()));

            ResultSet rs = checkCompatibilityStmt.executeQuery();
            rs.next();
            isCompatible = rs.getBoolean(1);

            if (isCompatible) {
                assignValuesToSlotTableInsertionStatement(appointment, insertSlotStmt);

                insertDescriptionStmt.setString(1, appointment.getId());
                insertDescriptionStmt.setString(2, appointment.getDescription());

                insertSlotStmt.executeUpdate();
                insertDescriptionStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException throwables) {
            throw new StorageException(throwables.getMessage());
        }

        return isCompatible;
    }

    /**
     * Get an appointment book that contains all bookable appointment slots belong to <code>owner</code> in the storage.
     *
     * @param owner the appointment owner
     * @return an appointment book that contains all bookable appointment slots
     * @throws StorageException If any error occurs during read/write with storage
     */
    @Override
    public AppointmentBook<AppointmentSlot> getAllBookableAppointmentSlotsByOwner(String owner) throws StorageException {
        String getAppointmentsSlots = "select * " +
                "from appointment_slots left join appointment_descriptions " +
                "on appointment_slots.id = appointment_descriptions.id " +
                "where appointment_slots.owner = ? and appointment_descriptions.description is null " +
                "and appointment_slots.slot_type = ?";
        AppointmentBook<AppointmentSlot> result = new AppointmentBook<>(owner);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement getAppointmentSlotsStmt = conn.prepareStatement(getAppointmentsSlots)) {
            getAppointmentSlotsStmt.setString(1, owner);
            getAppointmentSlotsStmt.setObject(2, AppointmentSlot.SlotType.OPEN_TO_EVERYONE, Types.OTHER);

            ResultSet rs = getAppointmentSlotsStmt.executeQuery();
            formAppointmentSlotQueryResults(result, rs);
        } catch (SQLException throwables) {
            throw new StorageException(throwables.getMessage());
        }

        if (result.getAppointments().isEmpty()) {
            return null;
        }
        return result;
    }

    /**
     * Add an appointment slot that is public bookable to <code>owner</code>
     *
     * @param owner the name of the owner
     * @param slot  an {@link AppointmentSlot} the is bookable
     * @return <code>true</code> if slot is added; <code>false</code> if slot to be added conflicts with existing
     * appointment slots owned by <code>owner</code>, thus cannot be added.
     * @throws StorageException If any error occurs during read/write with storage
     */
    @Override
    public boolean insertBookableAppointmentSlot(String owner, AppointmentSlot slot) throws StorageException {
        String compatibility = "select not exists (select 1 from appointment_slots " +
                "where (appointment_slots.owner = ? or appointment_slots.participator_identifier = ?) " +
                "and begin_time <= ? and end_time >= ?)";
        String insertIntoSlotTable = "insert into appointment_slots values (?, ?, ?, ?, ?, ?, ?)";
        boolean isCompatible;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement checkCompatibilityStmt = conn.prepareStatement(compatibility);
             PreparedStatement insertSlotStmt = conn.prepareStatement(insertIntoSlotTable)) {
            conn.setAutoCommit(false);

            checkCompatibilityStmt.setString(1, owner);
            checkCompatibilityStmt.setString(2, owner);
            checkCompatibilityStmt.setTimestamp(3, new Timestamp(slot.getEndTime().getTime()));
            checkCompatibilityStmt.setTimestamp(4, new Timestamp(slot.getBeginTime().getTime()));

            ResultSet rs = checkCompatibilityStmt.executeQuery();
            rs.next();
            isCompatible = rs.getBoolean(1);

            if (isCompatible) {
                assignValuesToSlotTableInsertionStatement(slot, insertSlotStmt);
                insertSlotStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException throwables) {
            throw new StorageException(throwables.getMessage());
        }

        return true;
    }

    /**
     * Add actual values to SQL statement used to insert a new appointment slot <code>slot</code>.
     *
     * @param slot the {@link AppointmentSlot} instance contains data to be insert into sql statement
     * @param insertSlotStmt the {@link PreparedStatement} instance
     * @throws SQLException any exception occurred when invoking method of {@link PreparedStatement}
     */
    private void assignValuesToSlotTableInsertionStatement(AppointmentSlot slot, PreparedStatement insertSlotStmt) throws SQLException {
        insertSlotStmt.setString(1, slot.getId());
        insertSlotStmt.setString(2, slot.getOwner());
        insertSlotStmt.setTimestamp(3, new Timestamp(slot.getBeginTime().getTime()));
        insertSlotStmt.setTimestamp(4, new Timestamp(slot.getEndTime().getTime()));
        insertSlotStmt.setObject(5, slot.getSlotType(), Types.OTHER);
        insertSlotStmt.setObject(6, slot.getParticipatorType() == null ? null :
                slot.getParticipatorType(), Types.OTHER);
        insertSlotStmt.setString(7, slot.getParticipatorIdentifier());
    }

    /**
     * Book an public bookable appointment slot of <code>owner</code>
     *
     * @param owner         the name of the owner
     * @param appointment   an {@link Appointment} instance to be booked
     * @param authenticated indicates if the booker is authenticated
     * @return {@link AppointmentBookStorage#NOT_BOOKABLE} if the appointment slot cannot be booked;
     * {@link AppointmentBookStorage#CONFLICT_WITH_EXISTING_APPOINTMENT} if the appointment slot conflicts with
     * existing appointment of authenticated user; otherwise, {@link AppointmentBookStorage#BOOK_SUCCESS}
     * @throws StorageException any exception occurred when invoking method of {@link PreparedStatement}
     */
    @Override
    public int bookAppointment(String owner, Appointment appointment, boolean authenticated) throws StorageException {
        String bookability = "select exists (select 1 from appointment_slots where " +
                "id = ? and " +
                "owner = ? and " +
                "begin_time = ? and " +
                "end_time = ? and " +
                "slot_type = ? and " +
                "participator_type is null and " +
                "participator_identifier is null), " +
                "not exists (select 1 from appointment_descriptions where id = ?)";
        String compatibility = "select not exists (select 1 from appointment_slots " +
                "where (appointment_slots.owner = ? or appointment_slots.participator_identifier = ?) " +
                "and begin_time <= ? and end_time >= ?)";
        String updateSlotTable = "update appointment_slots set " +
                "slot_type = ?, participator_type = ?, participator_identifier = ? where id = ?";
        String insertIntoDescriptionTable = "insert into appointment_descriptions values (?, ?)";
        boolean isCompatible;
        boolean isBookable;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement checkBookabilityStmt = conn.prepareStatement(bookability);
             PreparedStatement checkCompatibilityStmt = conn.prepareStatement(compatibility);
             PreparedStatement updateSlotStmt = conn.prepareStatement(updateSlotTable);
             PreparedStatement insertDescriptionStmt = conn.prepareStatement(insertIntoDescriptionTable)) {
            conn.setAutoCommit(false);

            checkBookabilityStmt.setString(1, appointment.getId());
            checkBookabilityStmt.setString(2, appointment.getOwner());
            checkBookabilityStmt.setTimestamp(3, new Timestamp(appointment.getBeginTime().getTime()));
            checkBookabilityStmt.setTimestamp(4, new Timestamp(appointment.getEndTime().getTime()));
            checkBookabilityStmt.setObject(5, AppointmentSlot.SlotType.OPEN_TO_EVERYONE, Types.OTHER);
            checkBookabilityStmt.setString(6, appointment.getId());

            checkCompatibilityStmt.setString(1, appointment.getParticipatorIdentifier());
            checkCompatibilityStmt.setString(2, appointment.getParticipatorIdentifier());
            checkCompatibilityStmt.setTimestamp(3, new Timestamp(appointment.getEndTime().getTime()));
            checkCompatibilityStmt.setTimestamp(4, new Timestamp(appointment.getBeginTime().getTime()));

            ResultSet rs = checkBookabilityStmt.executeQuery();
            rs.next();
            isBookable = rs.getBoolean(1) && rs.getBoolean(2);

            if (!isBookable) {
                return NOT_BOOKABLE;
            }

            if (authenticated) {
                rs = checkCompatibilityStmt.executeQuery();
                rs.next();
                isCompatible = rs.getBoolean(1);
                if (!isCompatible) {
                    return CONFLICT_WITH_EXISTING_APPOINTMENT;
                }
            }

            updateSlotStmt.setObject(1, AppointmentSlot.SlotType.PARTICIPATOR_BOOKED, Types.OTHER);
            if (authenticated) {
                updateSlotStmt.setObject(2, AppointmentSlot.ParticipatorType.REGISTERED, Types.OTHER);
            } else {
                updateSlotStmt.setObject(2, AppointmentSlot.ParticipatorType.UNREGISTERED, Types.OTHER);
            }
            updateSlotStmt.setString(3, appointment.getParticipatorIdentifier());
            updateSlotStmt.setString(4, appointment.getId());

            insertDescriptionStmt.setString(1, appointment.getId());
            insertDescriptionStmt.setString(2, appointment.getDescription());

            updateSlotStmt.executeUpdate();
            insertDescriptionStmt.executeUpdate();

            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return BOOK_SUCCESS;
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
        String compatibility = "select " +
                "exists (select 1 from user_profile where username = ?), " +
                "exists (select 1 from user_profile where email = ?)";
        String insertIntoUserTable = "insert into user_profile values (?, ?, ?, ?, ?)";
        int compatibleCheck;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement checkCompatibilityStmt = conn.prepareStatement(compatibility);
             PreparedStatement insertUserStmt = conn.prepareStatement(insertIntoUserTable)) {
            conn.setAutoCommit(false);

            checkCompatibilityStmt.setString(1, user.getUsername());
            checkCompatibilityStmt.setString(2, user.getEmail());

            ResultSet rs = checkCompatibilityStmt.executeQuery();
            rs.next();
            if (rs.getBoolean(1)) {
                compatibleCheck = USERNAME_CONFLICT;
            } else if (rs.getBoolean(2)) {
                compatibleCheck = EMAIL_CONFLICT;
            } else {
                insertUserStmt.setString(1, user.getId().toString());
                insertUserStmt.setString(2, user.getUsername());
                insertUserStmt.setString(3, user.getPassword());
                insertUserStmt.setString(4, user.getEmail());
                insertUserStmt.setString(5, user.getAddress());

                insertUserStmt.executeUpdate();

                compatibleCheck = REGISTER_USER_SUCCESS;
            }

            conn.commit();
        } catch (SQLException throwables) {
            throw new StorageException(throwables.getMessage());
        }

        return compatibleCheck;
    }

    /**
     * Retrieve a {@link User} by its username from the storage
     *
     * @param username the name of the user to retrieve
     * @return an <code>User</code> instance if found, <code>null</code> otherwise
     * @throws StorageException If any error occurs during read/write with storage
     */
    @Override
    public User getUserByUsername(String username) throws StorageException {
        String findUserByUsername = "select * from user_profile where username = ?";
        User user = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement findUserStmt = conn.prepareStatement(findUserByUsername)) {
            findUserStmt.setString(1, username);

            ResultSet rs = findUserStmt.executeQuery();
            if (rs.next()) {
                String id = rs.getString(1);
                String name = rs.getString(2);
                String password = rs.getString(3);
                String email = rs.getString(4);
                String address = rs.getString(5);

                user = new User(id, name, password, email, address);
            }
        } catch (SQLException throwables) {
            throw new StorageException(throwables.getMessage());
        }
        return user;
    }

}
