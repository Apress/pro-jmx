package com.apress.jhanson.remote;

import com.solers.slp.ServiceLocationException;
import com.solers.slp.ServiceURL;
import com.solers.slp.ServiceLocationManager;
import com.solers.slp.Advertiser;
import com.solers.slp.ServiceLocationAttribute;

import javax.management.remote.JMXServiceURL;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class SLPServer
{
  //The Service URL will remain registered for 300 secs.
  //
  public final static int JMX_DEFAULT_LEASE = 300;

  //Default scope.
  //
  public final static String JMX_SCOPE = "DEFAULT";

  public static void register(JMXServiceURL jmxUrl, String name)
    throws ServiceLocationException
  {
    // Create the SLP service URL.
    //
    // Note:It is recommended that the JMX agents make use of the
    // leasing feature of SLP,and periodically renew their lease.
    //
    ServiceURL serviceURL =
      new ServiceURL(jmxUrl.toString(),
                     JMX_DEFAULT_LEASE);

    // Prepare Lookup Attributes.
    //
    Vector attributes = new Vector();
    Vector attrValues = new Vector();

    // Specify default SLP scope.
    //
    attrValues.add(JMX_SCOPE);
    ServiceLocationAttribute attr1 =
      new ServiceLocationAttribute("SCOPE", attrValues);
    attributes.add(attr1);

    // Specify AgentName attribute (mandatory).
    //
    attrValues.removeAllElements();
    attrValues.add(name);
    ServiceLocationAttribute attr2 =
      new ServiceLocationAttribute("AgentName", attrValues);
    attributes.add(attr2);

    // Get SLP Advertiser.
    //
    final Advertiser slpAdvertiser =
      ServiceLocationManager.getAdvertiser(Locale.US);

    // Register the service:URL.
    //
    slpAdvertiser.register(serviceURL, attributes);
    System.out.println("\nRegistered URL:" + jmxUrl);
  }
}
