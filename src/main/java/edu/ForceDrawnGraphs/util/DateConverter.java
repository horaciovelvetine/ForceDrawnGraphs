package edu.ForceDrawnGraphs.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class DateConverter {
  public static String convertDate(String inputDate) {
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d, yyyy");

    try {
      Date date = inputFormat.parse(inputDate);
      return outputFormat.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }
}
