package edu.pdx.cs410J.yl6.database.plaintextoperator;

import edu.pdx.cs410J.yl6.Appointment;
import edu.pdx.cs410J.yl6.AppointmentSlot;

import java.io.Writer;

public abstract class AppointmentTableEntryDumper {

    public static class AppointmentDescriptionTableEntryDumper extends TableEntryDumper<Appointment> {

        public AppointmentDescriptionTableEntryDumper(Writer writer) {
            super(writer);
        }

        @Override
        public String[] getStringFields(Appointment appointment) {
            return new String[] { appointment.getId(), appointment.getDescription() };
        }
    }

    public static class AppointmentSlotTableEntryDumper extends TableEntryDumper<AppointmentSlot> {

        public AppointmentSlotTableEntryDumper(Writer writer) {
            super(writer);
        }

        @Override
        public String[] getStringFields(AppointmentSlot appointment) {
            return new String[] {
                    appointment.getOwner(),
                    appointment.getId(),
                    appointment.getBeginTimeString(),
                    appointment.getEndTimeString(),
                    appointment.getSlotType() == null ? null : appointment.getSlotType().toString(),
                    appointment.getParticipatorType() == null ? null : appointment.getParticipatorType().toString(),
                    appointment.getParticipatorIdentifier()
            };
        }
    }

}
