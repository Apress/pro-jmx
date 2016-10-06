package com.apress.jhanson.agents;

import com.apress.jhanson.services.*;
import com.sun.jdmk.comm.*;

import javax.management.*;
import javax.management.timer.Timer;
import javax.management.monitor.CounterMonitor;
import java.util.Hashtable;
import java.util.Set;
import java.util.Date;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class ServiceAgent
{
  static String counterMonitorClass =
    "javax.management.monitor.CounterMonitor";

  private MBeanServer mbServer = null;
  private ObjectName counterMonitorName = null;
  private CounterMonitor counterMonitor = null;
  private ObjectName timerOName = null;
  private ObjectName relationServiceOName = null;

  public static void main(String[] args)
  {
    ServiceAgent agent = new ServiceAgent();
  }

  public ServiceAgent()
  {
    try
    {
      mbServer = MBeanServerFactory.createMBeanServer();

      initializeCounterMonitor();
      initializeRelationService();

      ObjectName oName =
        new ObjectName("services:name=DateTime,type=information");
      mbServer.registerMBean(new DateTimeService(), oName);
      ObjectName adaptorOName =
        new ObjectName("adaptors:protocol=HTTP");
      HtmlAdaptorServer htmlAdaptor = new HtmlAdaptorServer();
      mbServer.registerMBean(htmlAdaptor, adaptorOName);
      htmlAdaptor.start();
    }
    catch (MBeanRegistrationException e)
    {
      e.printStackTrace();
    }
    catch (NotCompliantMBeanException e)
    {
      e.printStackTrace();
    }
    catch (MalformedObjectNameException e)
    {
      e.printStackTrace();
    }
    catch (InstanceAlreadyExistsException e)
    {
      e.printStackTrace();
    }
  }

  public MBeanServer getMBeanServer()
  {
    return mbServer;
  }

  public String getMBeanServerID()
  {
    try
    {
      ObjectName delegateOName =
        new ObjectName("JMImplementation:type=MBeanServerDelegate");
      return (String) mbServer.getAttribute(delegateOName, "MBeanServerId");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return "";
  }

  public void initializeRelationService()
  {
    try
    {
      relationServiceOName = new ObjectName(getRelationServiceDomain()
                                            + ":name=relationService");

      //Check to see if the relation service is already running.
      Set names = mbServer.queryNames(relationServiceOName, null);
      if (names != null && names.isEmpty() == false)
        return;

      Object[] params = new Object[1];
      params[0] = new Boolean(true);  //Purge invalid relations immediately.
      String[] signature = new String[1];
      signature[0] = "boolean";
      MBeanServer mbServer = MBeanServerFactory.createMBeanServer();
      mbServer.createMBean("javax.management.relation.RelationService",
                           relationServiceOName, params, signature);
    }
    catch (Exception e)
    {
      System.out.println(e.toString());
    }
  }

  public String getRelationServiceDomain()
  {
    return mbServer.getDefaultDomain();
  }

  public ObjectName getRelationServiceOName()
  {
    return relationServiceOName;
  }

  public void startTimer()
    throws ServiceAgentException
  {
    if (timerOName == null)
    {
      try
      {
        timerOName = new ObjectName("TimerServices:name=SimpleTimer");
        mbServer.registerMBean(new Timer(), timerOName);
        mbServer.invoke(timerOName, "start", null, null);
      }
      catch (Exception e)
      {
        throw new ServiceAgentException("Error starting timer:"
                                        + e.toString());
      }
    }
    else
    {
      try
      {
        if (((Boolean) mbServer.invoke(timerOName, "isActive",
                                       null, null)).booleanValue() == false)
        {
          mbServer.invoke(timerOName, "start", null, null);
        }
      }
      catch (Exception e)
      {
        throw new ServiceAgentException("Error starting timer:"
                                        + e.toString());
      }
    }
  }

  public void stopTimer()
    throws ServiceAgentException
  {
    if (timerOName != null)
    {
      try
      {
        if (((Boolean) mbServer.invoke(timerOName, "isActive",
                                       null, null)).booleanValue() == true)
        {
          mbServer.invoke(timerOName, "stop", null, null);
        }
      }
      catch (Exception e)
      {
        throw new ServiceAgentException("Error starting timer:"
                                        + e.toString());
      }
    }
  }

  public Integer addTimerNotification(String type,
                                      String message,
                                      Object userData,
                                      java.util.Date startDate,
                                      long period,
                                      long occurrences)
    throws ServiceAgentException
  {
    Object[] param = new Object[]
    {
      type,
      message,
      userData,
      startDate,
      new Long(period),
      new Long(occurrences)
    };

    String[] signature = new String[]
    {
      String.class.getName(),
      String.class.getName(),
      Object.class.getName(),
      Date.class.getName(),
      long.class.getName(),
      long.class.getName()
    };

    try
    {
      Object retVal = mbServer.invoke(timerOName, "addNotification",
                                      param, signature);
      return (Integer) retVal;
    }
    catch (Exception e)
    {
      throw new ServiceAgentException("Error adding notification:"
                                      + e.toString());
    }
  }

  public void removeTimerNotification(Integer id)
    throws ServiceAgentException
  {
    Object[] param = new Object[]
    {
      id
    };

    String[] signature = new String[]
    {
      Integer.class.getName()
    };

    try
    {
      mbServer.invoke(timerOName, "removeNotification", param, signature);
    }
    catch (Exception e)
    {
      throw new ServiceAgentException("Error removing notification:"
                                      + e.toString());
    }
  }

  public void addTimerListener(NotificationListener listener,
                               NotificationFilter filter,
                               Object handback)
    throws ServiceAgentException
  {
    try
    {
      mbServer.addNotificationListener(timerOName, listener,
                                       filter, handback);
    }
    catch (Exception e)
    {
      throw new ServiceAgentException("Error adding timer listener:"
                                      + e.toString());
    }
  }

  public void removeTimerListener(NotificationListener listener)
    throws ServiceAgentException
  {
    try
    {
      mbServer.removeNotificationListener(timerOName, listener);
    }
    catch (Exception e)
    {
      throw new ServiceAgentException("Error removing timer listener:"
                                      + e.toString());
    }
  }

  protected void initializeCounterMonitor()
  {
    counterMonitorName = null;
    counterMonitor = new CounterMonitor();

    // Get the domain name from the MBeanServer.
    //
    String domain = mbServer.getDefaultDomain();

    // Create a new CounterMonitor MBean and add it to the MBeanServer.
    //
    try
    {
      counterMonitorName = new ObjectName(domain + ":name="
                                          + counterMonitorClass);
    }
    catch (MalformedObjectNameException e)
    {
      e.printStackTrace();
      return;
    }

    try
    {
      mbServer.registerMBean(counterMonitor, counterMonitorName);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void setCounterMonitorListener(ObjectName observedObjName,
                                        NotificationListener listener,
                                        String attrName)
  {
    // Register a notification listener
    // with the CounterMonitor MBean,enabling the listener to receive
    // notifications transmitted by the CounterMonitor.
    //
    try
    {
      Integer threshold = new Integer(1);
      Integer offset = new Integer(1);
      counterMonitor.setObservedObject(observedObjName);
      counterMonitor.setObservedAttribute(attrName);
      counterMonitor.setNotify(true);
      counterMonitor.setThreshold(threshold);
      counterMonitor.setOffset(offset);
      counterMonitor.setGranularityPeriod(1000);

      NotificationFilter filter = null;
      Object handback = null;
      counterMonitor.addNotificationListener(listener, filter, handback);
      if (counterMonitor.isActive() == false)
        counterMonitor.start();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public ObjectInstance addResource(String name,
                                    Hashtable properties,
                                    String className)
    throws ServiceAgentException
  {
    ObjectInstance objInstance = null;
    try
    {
      Class cls = Class.forName(className);
      Object obj = cls.newInstance();
      Hashtable allProps = new Hashtable();
      allProps.put("name", name);
      properties.putAll(allProps);
      ObjectName oName = new ObjectName("services", allProps);
      objInstance = mbServer.registerMBean(obj, oName);
    }
    catch (IllegalAccessException e)
    {
      throw new ServiceAgentException(e.getMessage());
    }
    catch (InstantiationException e)
    {
      throw new ServiceAgentException("Unable to create instance of MBean:"
                                      + e.getMessage());
    }
    catch (ClassNotFoundException e)
    {
      throw new ServiceAgentException("Unable to find class for MBean:"
                                      + e.getMessage());
    }
    catch (MalformedObjectNameException e)
    {
      throw new ServiceAgentException("Invalid object name:"
                                      + e.getMessage());
    }
    catch (InstanceAlreadyExistsException e)
    {
      throw new ServiceAgentException("The MBean already exists:"
                                      + e.getMessage());
    }
    catch (MBeanRegistrationException e)
    {
      throw new ServiceAgentException("General registration exception:"
                                      + e.getMessage());
    }
    catch (NotCompliantMBeanException e)
    {
      throw new ServiceAgentException("The class is not MBean compliant:"
                                      + e.getMessage());
    }

    return objInstance;
  }

  public Set getResources(String name)
    throws ServiceAgentException
  {
    try
    {
      Hashtable allProps = new Hashtable();
      allProps.put("name", name);
      ObjectName oName = new ObjectName("services", allProps);
      Set resultSet = mbServer.queryMBeans(oName, null);
      return resultSet;
    }
    catch (MalformedObjectNameException e)
    {
      throw new ServiceAgentException("Invalid object name:"
                                      + e.getMessage());
    }
  }

  public void addMBeanServerNotificationListener(NotificationListener listener, Object handback)
  {
    try
    {
      ObjectName oName =
        new ObjectName("JMImplementation:type=MBeanServerDelegate");
      NotificationFilter filter = null;
      mbServer.addNotificationListener(oName, listener, filter, handback);
    }
    catch (MalformedObjectNameException e)
    {
      e.printStackTrace();
    }
    catch (InstanceNotFoundException e)
    {
      e.printStackTrace();
    }
  }


  public void removeMBeanNotificationListener(NotificationListener listener)
  {
    try
    {
      ObjectName oName =
        new ObjectName("JMImplementation:type=MBeanServerDelegate");
      mbServer.removeNotificationListener(oName, listener);
    }
    catch (MalformedObjectNameException e)
    {
      e.printStackTrace();
    }
    catch (NullPointerException e)
    {
      e.printStackTrace();
    }
    catch (InstanceNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (ListenerNotFoundException e)
    {
      e.printStackTrace();
    }
  }
}
