package com.apress.jhanson.remote;

import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;

import javax.management.remote.JMXConnector;
import java.io.IOException;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class ServiceRegistrar
{
  public static ServiceRegistration register(net.jini.core.lookup.ServiceRegistrar registrar,
                                             JMXConnector proxy,
                                             String name)
    throws IOException
  {
    //Prepare service's attributes entry.
    //
    Entry[] serviceAttrs = new Entry[] {
      new MyEntry(name)
      //Add here the lookup attributes you want to specify.
    };

    System.out.println("Registering proxy:AgentName=" + name);

    //Create a ServiceItem from the service instance.
    //
    ServiceItem srvcItem = new ServiceItem(null, proxy, serviceAttrs);

    //Register the service with the Lookup Service.
    //
    ServiceRegistration srvcRegistration =
      registrar.register(srvcItem, Lease.ANY);

    return srvcRegistration;
  }
}

class MyEntry
  implements Entry
{
  public String name;

  public MyEntry(String name)
  {
    this.name = name;
  }
}
