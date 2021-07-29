package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

public class Helper {

  private static String errorMessage = "";

  public static final String datePattern = "M/d/yyyy h:m a";

  public static String getDateString(Date d) {
    SimpleDateFormat outputDateFormat = new SimpleDateFormat(datePattern);
    return outputDateFormat.format(d);
  }

  public static Date validateAndParseDate(String s) {
    DateFormat df = new SimpleDateFormat(datePattern);
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

  public static boolean validateAndGetDateInterval(Date d1, Date d2, String mark1, String mark2) {
    DateFormat df = new SimpleDateFormat(datePattern);
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
}
