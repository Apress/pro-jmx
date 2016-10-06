package com.apress.jhanson.services;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */

import java.util.Calendar;
import java.text.DateFormat;

public class DateTimeService
  implements DateTimeServiceMBean
{
  public String getDate()
  {
    Calendar rightNow = Calendar.getInstance();
    return DateFormat.getDateInstance().format(rightNow.getTime());
  }

  public String getTime()
  {
    Calendar rightNow = Calendar.getInstance();
    return DateFormat.getTimeInstance().format(rightNow.getTime());
  }
}
