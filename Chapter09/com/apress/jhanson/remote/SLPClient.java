package com.apress.jhanson.remote;

import com.solers.slp.*;

import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import java.util.Vector;
import java.util.Locale;
import java.net.MalformedURLException;
import java.io.IOException;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class SLPClient
{
  public SLPClient()
  {
  }

  public void lookupAndConnect()
  {
    try
    {
      Locator slpLocator = com.solers.slp.ServiceLocationManager.getLocator(Locale.getDefault());
      // Lookup in default SCOPE.
      final Vector scopes = new Vector();
      scopes.add("DEFAULT");

      // Set the LDAPv3 query string.
      // Here you look for a specific agent called "my-jmx-agent",
      // but you could have asked for any agent by using a wildcard:
      // final String query ="(&(AgentName=*))";
      //
      final String query = "(&(AgentName=my-jmx-agent))";

      // Lookup
      final ServiceLocationEnumeration result =
        slpLocator.findServices(new ServiceType("service:jmx"), scopes, query);

      // Extract the list of returned ServiceURLs.
      while (result.hasMoreElements())
      {
        final ServiceURL surl = (ServiceURL) result.next();
        // Get the attributes.
        final ServiceLocationEnumeration slpAttributes =
          slpLocator.findAttributes(surl, scopes, new Vector());
        while (slpAttributes.hasMoreElements())
        {
          final ServiceLocationAttribute slpAttribute =
            (ServiceLocationAttribute) slpAttributes.nextElement();
          // ...
        }
        // Open a connection.
        final JMXServiceURL jmxUrl = new JMXServiceURL(surl.toString());
        final JMXConnector client = JMXConnectorFactory.connect(jmxUrl);
        // ...
      }
    }
    catch (ServiceLocationException e)
    {
      e.printStackTrace();
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
