package com.apress.jhanson.remote;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import java.io.IOException;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class GenericClient
{
  private MBeanServerConnection mbsc = null;

  public GenericClient()
  {
    try
    {
      int port = 3333;
      JMXServiceURL url = new JMXServiceURL("jmxmp", "myserver", port);
      JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
      mbsc = jmxc.getMBeanServerConnection();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
