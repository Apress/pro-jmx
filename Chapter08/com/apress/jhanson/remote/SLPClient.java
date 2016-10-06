package com.apress.jhanson.remote;

import com.solers.slp.*;

import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.List;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class SLPClient
{
  //Default scope.
  //
  public final static String JMX_SCOPE = "DEFAULT";

  public static List lookup(Locator slpLocator, String name)
    throws IOException, ServiceLocationException
  {
    //Set the query string.
    //
    //Will return only those services for which the AgentName
    //attribute was registered.Since JSR 160 specifies that
    //the AgentName attribute is mandatory,this makes it possible
    //to filter out all the services that do not conform
    //to the spec.
    //If <name>is null,it is replaced by "*", so that all
    //services for which the AgentName attribute was specified match,
    //regardless of the value of that attribute.Otherwise,only
    //those services for which AgentName matches the
    //name or pattern specified by <name>will be returned.
    //
    String query = "(&(AgentName=" + ((name != null) ? name : "*") + "))";

    //Set the lookup scope.
    //
    Vector scopes = new Vector();
    scopes.add(JMX_SCOPE);

    // Lookup the JMX agents
    //
    ServiceLocationEnumeration result =
      slpLocator.findServices(new ServiceType("service:jmx"),
                              scopes, query);

    final ArrayList list = new ArrayList();

    //Build the JMXConnector list.
    //
    while (result.hasMoreElements())
    {
      final ServiceURL surl = (ServiceURL) result.next();

      //Retrieve the Lookup Attributes that were registered
      //with this URL.
      //
      final ServiceLocationEnumeration slpAttributes =
        slpLocator.findAttributes(surl, scopes, new Vector());

      while (slpAttributes.hasMoreElements())
      {
        final ServiceLocationAttribute slpAttribute =
          (ServiceLocationAttribute) slpAttributes.nextElement();
        System.out.println("Attribute: " + slpAttribute);
      }

      //Create a JMX service URL.
      //
      JMXServiceURL jmxUrl = new JMXServiceURL(surl.toString());
      try
      {
        //Create a JMXConnector using the JMXConnectorFactory.
        //
        JMXConnector client =
          JMXConnectorFactory.newJMXConnector(jmxUrl, null);
        System.out.println("JMX Connector:" + client);

        //Add the connector to the result list.
        //
        if (client != null)
          list.add(client);
      }
      catch (IOException e)
      {
        System.err.println("Failed to create JMXConnector for " + jmxUrl);
        System.err.println("Error is:" + e);
      }
    }

    return list;
  }
}
