package edu.pdx.cs410J.yl6;

import androidx.room.TypeConverter;

import java.util.Date;
import java.util.UUID;

public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Appointment.SlotType slotTypeFromString(String value) {
        return value == null ? null : Appointment.SlotType.valueOf(value);
    }

    @TypeConverter
    public static String slotTypeToString(Appointment.SlotType slotType) {
        return slotType == null ? null : slotType.name();
    }

    @TypeConverter
    public static Appointment.ParticipatorType participatorTypeFromString(String value) {
        return value == null ? null : Appointment.ParticipatorType.valueOf(value);
    }

    @TypeConverter
    public static String participatorTypeToString(Appointment.ParticipatorType participatorType) {
        return participatorType == null ? null : participatorType.name();
    }

    @TypeConverter
    public static UUID uuidFromString(String value) {
        return UUID.fromString(value);
    }

    @TypeConverter
    public static String uuidToString(UUID uuid) {
        return uuid.toString();
    }
}