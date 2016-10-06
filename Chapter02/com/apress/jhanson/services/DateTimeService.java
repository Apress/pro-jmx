package com.apress.jhanson.services;

import javax.management.*;
import java.util.Calendar;
import java.util.Iterator;
import java.text.DateFormat;
import java.lang.reflect.Constructor;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class DateTimeService
  implements DynamicMBean
{
  public static final boolean READABLE = true;
  public static final boolean WRITEABLE = true;
  public static final boolean ISIS = true;
  private String userConfiguredDate = null;
  private String userConfiguredTime = null;
  private MBeanAttributeInfo[] attributeInfo = new MBeanAttributeInfo[2];
  private MBeanConstructorInfo[] constructorInfo = new MBeanConstructorInfo[1];
  private MBeanOperationInfo[] operationInfo = new MBeanOperationInfo[2];
  private MBeanInfo mBeanInfo = null;
  private MBeanNotificationInfo[] notificationInfo = null;
  private NotificationBroadcasterSupport broadcasterSupport =
    new NotificationBroadcasterSupport();
  private long notificationSequence = 0;

  public void setDate(String newValue)
  {
    String oldValue = getDate();
    String attrType = String.class.getName();
    String attrName = "Date";
    userConfiguredDate = newValue;

    AttributeChangeNotification notif =
      new AttributeChangeNotification(this,
                                      ++notificationSequence,
                                      System.currentTimeMillis(),
                                      "Date has been changed.",
                                      attrName, attrType,
                                      oldValue, newValue);
    broadcasterSupport.sendNotification(notif);
  }

  public String getDate()
  {
    if (userConfiguredDate != null)
      return userConfiguredDate;
    Calendar rightNow = Calendar.getInstance();
    return DateFormat.getDateInstance().format(rightNow.getTime());
  }

  public void setTime(String newValue)
  {
    String oldValue = getDate();
    String attrType = String.class.getName();
    String attrName = "Time";
    userConfiguredTime = newValue;
    AttributeChangeNotification notif =
      new AttributeChangeNotification(this,
                                      ++notificationSequence,
                                      System.currentTimeMillis(),
                                      "Time has been changed.",
                                      attrName, attrType,
                                      oldValue, newValue);
    broadcasterSupport.sendNotification(notif);
  }

  public String getTime()
  {
    if (userConfiguredTime != null)
      return userConfiguredTime;
    Calendar rightNow = Calendar.getInstance();
    return DateFormat.getTimeInstance().format(rightNow.getTime());
  }

  public void stop()
  {
    Notification notif = new Notification("services.datetime.stop",
                                          this,
                                          ++notificationSequence,
                                          "DateTime service stopped.");
    broadcasterSupport.sendNotification(notif);
  }

  public void start()
  {
    Notification notif = new Notification("services.datetime.start",
                                          this,
                                          ++notificationSequence,
                                          "DateTime service started.");
    broadcasterSupport.sendNotification(notif);
  }

  public Object getAttribute(String attributeName)
    throws AttributeNotFoundException,
    MBeanException,
    ReflectionException
  {
    if (attributeName == null)
    {
      IllegalArgumentException ex =
        new IllegalArgumentException("Attribute name cannot be null");
      throw new RuntimeOperationsException(ex, "null attribute name");
    }
    if (attributeName.equals("Date"))
    {
      return getDate();
    }
    if (attributeName.equals("Time"))
    {
      return getTime();
    }
    throw(new AttributeNotFoundException("Invalid attribute:"
                                         + attributeName));
  }

  public void setAttribute(Attribute attribute)
    throws AttributeNotFoundException,
    InvalidAttributeValueException,
    MBeanException,
    ReflectionException
  {
    if (attribute == null)
    {
      IllegalArgumentException ex =
        new IllegalArgumentException("Attribute cannot be null");
      throw new RuntimeOperationsException(ex, "null attribute");
    }
    String name = attribute.getName();
    Object value = attribute.getValue();
    if (name == null)
    {
      IllegalArgumentException ex =
        new IllegalArgumentException("Attribute name cannot be null");
      throw new RuntimeOperationsException(ex, "null attribute name");
    }

    if (value == null)
    {
      IllegalArgumentException ex =
        new IllegalArgumentException("Attribute value cannot be null");
      throw new RuntimeOperationsException(ex, "null attribute value");
    }

    try
    {
      Class stringCls = Class.forName("java.lang.String");
      if (stringCls.isAssignableFrom(value.getClass()) == false)
      {
        IllegalArgumentException ex =
          new IllegalArgumentException("Invalid attribute value class");
        throw new RuntimeOperationsException(ex, "Invalid attribute value");
      }
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }

    if (name.equals("Date"))
    {
      setDate(value.toString());
    }
    else if (name.equals("Time"))
    {
      setTime(value.toString());
    }
    else
    {
      throw(new AttributeNotFoundException("Invalid Attribute name;"
                                           + name));
    }
  }

  public AttributeList getAttributes(String[] attributeNames)
  {
    if (attributeNames == null)
    {
      IllegalArgumentException ex =
        new IllegalArgumentException("attributeNames cannot be null");
      throw new RuntimeOperationsException(ex, "null attribute names");
    }
    AttributeList resultList = new AttributeList();
    if (attributeNames.length == 0)
      return resultList;
    for (int i = 0; i < attributeNames.length; i++)
    {
      try
      {
        Object value = getAttribute((String) attributeNames[i]);
        resultList.add(new Attribute(attributeNames[i], value));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    return (resultList);
  }

  public AttributeList setAttributes(AttributeList attributes)
  {
    if (attributes == null)
    {
      IllegalArgumentException ex =
        new IllegalArgumentException("attributes cannot be null");
      throw new RuntimeOperationsException(ex, "null attribute list");
    }
    AttributeList resultList = new AttributeList();
    if (attributes.isEmpty())
      return resultList;
    for (Iterator i = attributes.iterator(); i.hasNext();)
    {
      Attribute attr = (Attribute) i.next();
      try
      {
        setAttribute(attr);
        String name = attr.getName();
        Object value = getAttribute(name);
        resultList.add(new Attribute(name, value));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    return (resultList);
  }

  public Object invoke(String operationName,
                       Object[] params,
                       String[] signature)
    throws MBeanException,
    ReflectionException
  {
    if (operationName == null)
    {
      IllegalArgumentException ex =
        new IllegalArgumentException("Operation name cannot be null");
      throw new RuntimeOperationsException(ex, "null operation name");
    }
    if (operationName.equals("stop"))
    {
      stop();
      return null;
    }
    else if (operationName.equals("start"))
    {
      start();
      return null;
    }
    else
    {
      throw new ReflectionException(new NoSuchMethodException(operationName),
                                    "Invalid operation name:"
                                    + operationName);
    }
  }

  public MBeanInfo getMBeanInfo()
  {
    if (mBeanInfo != null)
      return mBeanInfo;
    attributeInfo[0] = new MBeanAttributeInfo("Date",
                                              String.class.getName(),
                                              "The current date.",
                                              READABLE,
                                              WRITEABLE,
                                              !ISIS);
    attributeInfo[1] = new MBeanAttributeInfo("Time",
                                              String.class.getName(),
                                              "The current time",
                                              READABLE,
                                              WRITEABLE,
                                              !ISIS);
    Constructor[] constructors = this.getClass().getConstructors();
    constructorInfo[0] =
      new MBeanConstructorInfo("Constructs a DateTimeService object",
                               constructors[0]);
    MBeanParameterInfo[] params = null;
    operationInfo[0] = new MBeanOperationInfo("start",
                                              "Starts the DateTime service",
                                              params,
                                              "void",
                                              MBeanOperationInfo.ACTION);
    operationInfo[1] = new MBeanOperationInfo("stop",
                                              "Stops the DateTime service",
                                              params,
                                              "void",
                                              MBeanOperationInfo.ACTION);
    mBeanInfo = new MBeanInfo(this.getClass().getName(),
                              "DateTime Service MBean",
                              attributeInfo,
                              constructorInfo,
                              operationInfo,
                              getNotificationInfo());
    return (mBeanInfo);
  }

  public MBeanNotificationInfo[] getNotificationInfo()
  {
    if (notificationInfo != null)
      return notificationInfo;
    notificationInfo = new MBeanNotificationInfo[]
    {
      new MBeanNotificationInfo(new String[] {"service.user.start"},
                                Notification.class.getName(),
                                "DateTime service start."),
      new MBeanNotificationInfo(new String[] {"service.user.stop"},
                                Notification.class.getName(),
                                "DateTime service stop."),
      new MBeanNotificationInfo(new String[]{
        AttributeChangeNotification.ATTRIBUTE_CHANGE},
                                AttributeChangeNotification.class.getName(),
                                "DateTime service attribute changes.")
    };
    return notificationInfo;
  }

  public void addNotificationListener(NotificationListener listener,
                                      NotificationFilter filter,
                                      Object handback)
  {
    broadcasterSupport.addNotificationListener(listener, filter, handback);
  }

  public void removeNotificationListener(NotificationListener listener)
    throws ListenerNotFoundException
  {
    broadcasterSupport.removeNotificationListener(listener);
  }

  public void removeNotificationListener(NotificationListener listener,
                                         NotificationFilter filter,
                                         Object handback)
    throws ListenerNotFoundException
  {
    broadcasterSupport.removeNotificationListener(listener, filter, handback);
  }
}
