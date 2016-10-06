package com.apress.jhanson.remote;

import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.entry.Entry;

import javax.management.remote.JMXConnector;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class JINIConnectorUtil
{
  public static List lookup(net.jini.core.lookup.ServiceRegistrar registrar, String name)
    throws IOException
  {
    final ArrayList list = new ArrayList();

    // Returns only JMXConnectors. The filter could be made
    // more strict by supplying e.g. RMIConnector.class
    // (would only return RMIConnectors) or JMXMPConnector.class
    // (would only return JMXMPConnectors) etc.
    //
    final Class[] classes = new Class[]{JMXConnector.class};

    // Will return only those services for which the Name
    // attribute was registered.Since JSR 160 specifies that
    // the Name attribute is mandatory,this makes it possible
    // to filter out all the services that do not conform
    // to the spec.
    // If <name>is null,then all services for which the
    // Name attribute was specified will match,regardless of
    // the value of that attribute.Otherwise,only those services
    // for which Name matches the specified name will be returned.
    //
    final Entry[] serviceAttrs = new Entry[] {
      // Add here the matching attributes.
      new MyConnectorEntry(name)
    };

    // Create a ServiceTemplate to do the matching.
    //
    ServiceTemplate template =
      new ServiceTemplate(null, classes, serviceAttrs);

    // Look up all matching services in the Jini Lookup Service.
    //
    ServiceMatches matches =
      registrar.lookup(template, Integer.MAX_VALUE);

    // Retrieve the matching JMX connectors.
    //
    for (int i = 0; i < matches.totalMatches; i++)
    {
      if (matches.items[i].service != null)
      {
        // Service could be null if it can't be deserialized, because
        // e.g.,the class was not found.
        // This will not happen with JSR 160 mandatory connectors
        // however.
        // Get the JMXConnector.
        //
        JMXConnector c = (JMXConnector) (matches.items[i].service);

        //Add the connector to the result list.
        list.add(c);
      }
    }
    return list;
  }
}

class MyConnectorEntry
  implements Entry
{
  public String name;

  public MyConnectorEntry(String name)
  {
    this.name = name;
  }
}
