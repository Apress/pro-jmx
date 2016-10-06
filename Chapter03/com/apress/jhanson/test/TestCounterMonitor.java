package com.apress.jhanson.test;

import com.apress.jhanson.agents.ServiceAgent;
import com.apress.jhanson.agents.ServiceAgentException;

import javax.management.ObjectInstance;
import javax.management.NotificationListener;
import javax.management.Notification;
import javax.management.monitor.MonitorNotification;
import javax.management.monitor.Monitor;
import java.util.Properties;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class TestCounterMonitor
  implements NotificationListener
{
  public static void main(String[] args)
  {
  }

  public TestCounterMonitor()
  {
    ServiceAgent serviceAgent = new ServiceAgent();
    Properties properties = new Properties();
    properties.put("type", "DateTimeService");
    String mbeanClassName = "com.apress.jhanson.services.DateTimeService";
    try
    {
      ObjectInstance objInst = serviceAgent.addResource("DateTime",
                                                        properties,
                                                        mbeanClassName);
      serviceAgent.setCounterMonitorListener(objInst.getObjectName(),
                                             this,
                                             "Second");
    }
    catch (ServiceAgentException e)
    {
      System.err.println(e);
    }
  }

  public void handleNotification(Notification notification, Object handback)
  {
    if (notification instanceof MonitorNotification)
    {
      MonitorNotification notif = (MonitorNotification) notification;
      
      //Get monitor responsible for the notification.
      //
      Monitor monitor = (Monitor) notif.getSource();

      //Test the notification types transmitted by the monitor.
      String t = notif.getType();
      Object observedObj = notif.getObservedObject();
      String observedAttr = notif.getObservedAttribute();
      try
      {
        if (t.equals(MonitorNotification.OBSERVED_OBJECT_ERROR))
        {
          System.out.println(observedObj.getClass().getName()
                             + "is not registered in the server");
        }
        else if (t.equals(MonitorNotification.OBSERVED_ATTRIBUTE_ERROR))
        {
          System.out.println(observedAttr + "is not contained in " +
                             observedObj.getClass().getName());
        }
        else if (t.equals(MonitorNotification.OBSERVED_ATTRIBUTE_TYPE_ERROR))
        {
          System.out.println(observedAttr + "type is not correct");
        }
        else if (t.equals(MonitorNotification.THRESHOLD_ERROR))
        {
          System.out.println("Threshold type is incorrect");
        }
        else if (t.equals(MonitorNotification.RUNTIME_ERROR))
        {
          System.out.println("Unknown runtime error");
        }
        else if (t.equals(MonitorNotification.THRESHOLD_VALUE_EXCEEDED))
        {
          System.out.println("observedAttr"
                             + "has reached the threshold \n");
        }
        else
        {
          System.out.println("Unknown event type");
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      System.out.println("Received notification: " + notification.getMessage());
    }
  }
}
