package com.apress.jhanson.remote;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnector;
import javax.management.*;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class Agent
{
  private MBeanServer mBeanServer = null;
  private JMXServiceURL url = null;

  public Agent()
  {
  }

  protected void initializeGeneric()
  {
    mBeanServer =
      MBeanServerFactory.createMBeanServer();
    try
    {
      url = new JMXServiceURL("jmxmp", null, 5678);
      JMXConnectorServer cs =
        JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
      ObjectName csName = new ObjectName(":type=cserver,name=mycserver");
      mBeanServer.registerMBean(cs, csName);
      cs.start();
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
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
    catch (MBeanRegistrationException e)
    {
      e.printStackTrace();
    }
    catch (NotCompliantMBeanException e)
    {
      e.printStackTrace();
    }
  }

  protected void initializeRMI()
  {
    mBeanServer =
      MBeanServerFactory.createMBeanServer();
    try
    {
      url =
        new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:5678/server");
      JMXConnectorServer cs =
        JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
      ObjectName csName = new ObjectName(":type=cserver,name=mycserver");
      mBeanServer.registerMBean(cs, csName);
      cs.start();
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
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
    catch (MBeanRegistrationException e)
    {
      e.printStackTrace();
    }
    catch (NotCompliantMBeanException e)
    {
      e.printStackTrace();
    }
  }
}
