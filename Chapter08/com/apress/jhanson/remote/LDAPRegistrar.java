package com.apress.jhanson.remote;

import javax.naming.directory.*;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.management.remote.JMXServiceURL;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Date;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class LDAPRegistrar
{
  // The Service URL will remain registered for 300 secs.
  //
  public final static int JMX_DEFAULT_LEASE = 300;

  public static void register(DirContext root,
                              JMXServiceURL jmxUrl,
                              String name)
    throws NamingException, IOException
  {
    // Get the LDAP DN where to register.
    //
    final String mydn = System.getProperty("dn", "cn=" + name);

    // First check whether <mydn>already exists.
    //
    Object o = null;
    try
    {
      o = root.lookup(mydn);
      // There is already a node at <mydn>.
      //
    }
    catch (NameNotFoundException n)
    {
      // <mydn>does not exist!Attempt to create it.
      //
      // Prepare attributes for creating a javaContainer.
      //
      Attributes attrs = new BasicAttributes();

      // Prepare objectClass attribute:you're going to create a
      // javaContainer.
      //
      Attribute objclass = new BasicAttribute("objectClass");
      objclass.add("top");
      objclass.add("javaContainer");
      attrs.put(objclass);
      o = root.createSubcontext(mydn, attrs);
    }
    // Add the jmxConnector objectClass if needed.
    //
    final Attributes attrs = root.getAttributes(mydn);
    final Attribute oc = attrs.get("objectClass");
    if (!oc.contains("jmxConnector"))
    {
      // The node does not have the jmxConnector AUXILIARY class.
      // Try to add it.
      //
      final Attributes add = new BasicAttributes();
      add.put("objectClass", "jmxConnector");

      // jmxAgentName is a mandatory attribute for a jmxConnector.
      //
      add.put("jmxAgentName", name);

      // Add the jmxConnector object class and jmxAgentName attribute.
      //
      root.modifyAttributes(mydn, DirContext.ADD_ATTRIBUTE, add);
    }

    // Now you need to replace jmxConnector attributes.
    //
    final Attributes newattrs = new BasicAttributes();
    newattrs.put("jmxServiceUrl", jmxUrl.toString());
    newattrs.put("jmxAgentName", name);
    newattrs.put("jmxProtocolType", jmxUrl.getProtocol());
    newattrs.put("jmxAgentHost", InetAddress.getLocalHost().getHostName());
    newattrs.put("jmxExpirationDate",
                 getExpirationDate(JMX_DEFAULT_LEASE));
    newattrs.put(new BasicAttribute("jmxProperty"));
    root.modifyAttributes(mydn, DirContext.REPLACE_ATTRIBUTE, newattrs);
  }

  private static String getExpirationDate(int lease)
  {
    return "" + (new Date().getTime() + lease);
  }
}
