package com.apress.jhanson.remote;

import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.naming.directory.*;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class LDAPConnectorUtil
{
  public static List lookup(DirContext root, String protocolType, String name)
    throws IOException, NamingException
  {
    final ArrayList list = new ArrayList();

    // If protocolType is not null,include it in the filter.
    //
    String queryProtocol =
      (protocolType == null) ? "" : "(jmxProtocolType=" + protocolType + ")";

    // Set the LDAPv3 query string.
    //
    // Only those nodes that have the jmxConnector object class are
    // of interest,so you specify (objectClass=jmxConnector)
    // in the filter.
    //
    // Specify the jmxAgentName attribute in the filter so that the
    // query will return only those services for which the AgentName
    // attribute was registered.Since JSR 160 specifies that
    // the AgentName attribute is mandatory,this makes it possible
    // to filter out all the services that do not conform
    // to the spec.

    //
    // If <name>is null,it is replaced by "*",so that all
    // services for which the AgentName attribute was specified match,
    // regardless of the value of that attribute.
    // Otherwise,only those services for which AgentName matches the
    // name or pattern specified by <name>will be returned.
    //
    // Also specify (jmxServiceURL=*)so that only those nodes
    // for which the jmxServiceURL attribute is present will be
    // returned.Thus,you filter out all those nodes corresponding
    // to agents that are not currently available.
    //
    String query = "(&" + "(objectClass=jmxConnector)" +
      "(jmxServiceURL=*)" +
      queryProtocol +
      "(jmxAgentName=" + ((name != null) ? name : "*") + "))";
    System.out.println("Looking up JMX Agents with filter:" + query);
    SearchControls ctrls = new SearchControls();

    // Want to get all jmxConnector objects,wherever they've been
    // registered.
    //
    ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    // Search
    //
    final NamingEnumeration results = root.search("", query, ctrls);
    for (; results.hasMore();)
    {
      // Get result.
      //
      final SearchResult r = (SearchResult) results.nextElement();

      //Get attributes.
      //
      final Attributes attrs = r.getAttributes();

      //Get jmxServiceURL attribute.
      //
      final Attribute attr = attrs.get("jmxServiceURL");
      if (attr == null)
        continue;

      // Get jmxExpirationDate.
      //
      final Attribute exp = attrs.get("jmxExpirationDate");

      // Check that URL has not expired.
      //
      if ((exp != null) && hasExpired((String)exp.get()))
      {
        continue;
      }

      // Get the URL string.
      //
      final String urlStr = (String) attr.get();
      if (urlStr.length() == 0)
        continue;

      // Create a JMXServiceURL.
      //
      final JMXServiceURL url = new JMXServiceURL(urlStr);

      // Create a JMXConnector.
      //
      final JMXConnector conn =
        JMXConnectorFactory.newJMXConnector(url, null);

      // Add the connector to the result list.
      //
      list.add(conn);
    }

    return list;
  }

  private static boolean hasExpired(String s)
  {
    // test for expiration here
    return false;
  }
}
