package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.util.AbstractSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

public class AppointmentSlotAllocator {

  private AbstractSet<AppointmentSlot> slots;

  public AppointmentSlotAllocator() {
    this.slots = new HashSet<>();
  }

  public AppointmentSlotAllocator(Date begin, Date end, int duration, int gap) {
    this.slots = new HashSet<>();
    Date b = begin;
    Date e = null;
    if (!(e = incrementMinute(b, duration + gap)).after(end)) {
      this.slots.add(new AppointmentSlot(b, e));
      b = e;
    }
  }

  private Date incrementMinute(Date d, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(d);
    calendar.add(Calendar.MINUTE, amount);
    return calendar.getTime();
  }

  public Collection<AppointmentSlot> getSlots() {
    return this.slots;
  }

  public void addSlot(AppointmentSlot slot) {
    this.slots.add(slot);
  }

  /**
   * Given a collection of slots stored in {@link AppointmentSlotAllocator}, for
   * each slot in <code>slots</code>, add it if and only if it does not conflict
   * with any slot in invoking instance.
   * 
   * @param slots the appointment slots to add
   */
  public void joinSlots(AppointmentSlotAllocator slots) {
    for (AppointmentSlot slot : slots.getSlots()) {
      this.slots.add(slot);
    }
  }

  /**
   * Given a collection of slots stored in {@link AppointmentSlotAllocator},
   * remove any slot stored in invoking instance such that it conflicts with any
   * slot in <code>slots</code>
   * 
   * @param slots the appointment slots to avoid confliction
   */
  public void removeConflicts(AppointmentSlotAllocator slots) {
    Collection<AppointmentSlot> restrictions = slots.getSlots();
    this.slots.removeIf(slot -> restrictions.contains(slot));
  }

}
