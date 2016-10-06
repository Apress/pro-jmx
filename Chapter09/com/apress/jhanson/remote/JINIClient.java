package com.apress.jhanson.remote;

import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.entry.Entry;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import java.util.HashMap;
import java.util.Map;
import java.rmi.RemoteException;
import java.io.IOException;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class JINIClient
{
  public JINIClient()
  {
  }

  public void lookupAndConnect(ServiceRegistrar registrar)
  {
    // Prepare Service's attributes entry to be matched.
    Entry[] serviceAttrs = new Entry[] {
      new MyJINIEntry()
      // Add here any other matching attribute.
    };

    // Look for all types of JMXConnector.
    //
    ServiceTemplate template = new ServiceTemplate(null,
                                                   new Class[]{JMXConnector.class},
                                                   serviceAttrs);
    ServiceMatches matches = null;
    try
    {
      matches = registrar.lookup(template, Integer.MAX_VALUE);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }

    // Retrieve the JMX Connector and initiate a connection.
    //
    for (int i = 0; i < matches.totalMatches; i++)
    {
      if (matches.items[i].service != null)
      {
        // Get the JMXConnector.
        JMXConnector c = (JMXConnector)(matches.items[i].service);

        // Prepare env (security parameters etc...).
        Map env = new HashMap();
        // env.put(...);

        //Initiate the connection.
        try
        {
          c.connect(env);

          //Get the remote MBeanServer handle.
          MBeanServerConnection server = c.getMBeanServerConnection();
          // ...
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
  }
}

class MyJINIEntry
  implements net.jini.core.entry.Entry
{
}
