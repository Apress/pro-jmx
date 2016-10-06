package com.apress.jhanson.services;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public interface DateTimeServiceMBean
{
  public void setDate(String newValue);

  public String getDate();

  public void setTime(String newValue);

  public String getTime();

  public void stop();

  public void start();
}
