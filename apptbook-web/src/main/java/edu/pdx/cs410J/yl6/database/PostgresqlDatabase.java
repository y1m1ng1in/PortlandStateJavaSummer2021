package edu.pdx.cs410J.yl6.database;

import edu.pdx.cs410J.yl6.Appointment;
import edu.pdx.cs410J.yl6.AppointmentBook;
import edu.pdx.cs410J.yl6.AppointmentSlot;
import edu.pdx.cs410J.yl6.User;
import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class PostgresqlDatabase implements AppointmentBookStorage {

    private static final String connectionUrl = "jdbc:postgresql://localhost:5432/postgres";
    private static volatile AppointmentBookStorage INSTANCE;

    private final PoolingDataSource<PoolableConnection> dataSource;

    private PostgresqlDatabase() {
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectionUrl, "postgres",
                "971013970206Lym");
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
        dataSource = new PoolingDataSource<>(connectionPool);

        String createSlotTypeSql = String.format("CREATE TYPE appointment_slot_type AS ENUM ('%s', '%s', '%s')",
                AppointmentSlot.SlotType.OPEN_TO_EVERYONE, AppointmentSlot.SlotType.PARTICIPATOR_BOOKED,
                AppointmentSlot.SlotType.OWNER_SELF_ADDED);

        String createParticipatorTypeSql = String.format("CREATE TYPE appointment_participator_type AS ENUM ('%s', '%s')",
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
                "id varchar(20) not null," +
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

    public static AppointmentBookStorage getDatabase() {
        if (INSTANCE == null) {
            synchronized (PostgresqlDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PostgresqlDatabase();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public AppointmentBook<Appointment> getAllAppointmentsByOwner(String owner) throws StorageException {
        return null;
    }

    @Override
    public AppointmentBook<Appointment> getAppointmentsByOwnerWithBeginInterval(String owner, Date from, Date to) throws StorageException {
        return null;
    }

    @Override
    public boolean insertAppointmentWithOwner(String owner, Appointment appointment) throws StorageException {
        return false;
    }

    @Override
    public AppointmentBook<AppointmentSlot> getAllBookableAppointmentSlotsByOwner(String owner) throws StorageException {
        return null;
    }

    @Override
    public AppointmentBook<AppointmentSlot> getAllExistingAppointmentSlotsByOwner(String owner) throws StorageException {
        return null;
    }

    @Override
    public boolean insertBookableAppointmentSlot(String owner, AppointmentSlot slot) throws StorageException {
        return false;
    }

    @Override
    public int bookAppointment(String owner, Appointment appointment, boolean authenticated) throws StorageException {
        return 0;
    }

    @Override
    public void insertUser(User user) throws StorageException {

    }

    @Override
    public User getUserByUsername(String username) throws StorageException {
        return null;
    }

}
