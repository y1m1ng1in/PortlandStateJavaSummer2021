package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

public class Helper {

  private static String errorMessage = "";

  private static Date lower_date;
  private static Date upper_date;

  public static final String datePattern = "M/d/yyyy h:m a";

  public static String getDateString(Date d) {
    SimpleDateFormat outputDateFormat = new SimpleDateFormat(datePattern, Locale.US);
    return outputDateFormat.format(d);
  }

  public static Date validateAndParseDate(String s) {
    DateFormat df = new SimpleDateFormat(datePattern, Locale.US);
    df.setLenient(false);
    Date d = null;
    try {
      d = df.parse(s);
    } catch (ParseException e) {
      errorMessage = e.getMessage();
      return null;
    }
    return d;
  }

  public static boolean validateDate(String s) {
    if (validateAndParseDate(s) == null) {
      return false;
    }
    return true;
  }

  public static boolean validateTwoDateStringForDateInterval(String d1, String d2, String mark1, String mark2) {
    Date date1 = validateAndParseDate(d1);
    if (date1 == null) {
      return false;
    }
    Date date2 = validateAndParseDate(d2);
    if (date2 == null) {
      return false;
    }
    lower_date = date1;
    upper_date = date2;
    return validateAndGetDateInterval(date1, date2, mark1, mark2);
  }

  public static boolean validateAndGetDateInterval(Date d1, Date d2, String mark1, String mark2) {
    DateFormat df = new SimpleDateFormat(datePattern, Locale.US);
    df.setLenient(false);
    if (!d1.before(d2)) {
      errorMessage = String.format("%s %s is later than %s %s", mark1, df.format(d1), mark2, df.format(d2));
      return false;
    }
    return true;
  }

  public static String getErrorMessage() {
    return errorMessage;
  }

  public static Date getLowerDate() {
    return lower_date;
  }

  public static Date getUpperDate() {
    return upper_date;
  }
}
