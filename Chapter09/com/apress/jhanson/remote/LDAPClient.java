package com.apress.jhanson.remote;

import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.naming.directory.*;
import javax.naming.NamingEnumeration;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.io.IOException;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class LDAPClient
{
  public LDAPClient()
  {
  }

  public void lookupAndConnect()
  {
    try
    {
      // Create initial context
      //
      Hashtable env = new Hashtable();
      env.put(InitialContext.PROVIDER_URL, "ldap://ldap.someserver.com");
      // env.put(...);

      InitialDirContext root = new InitialDirContext(env);

      // Prepare search filter expression to use for the search.
      // The interpretation of the filter is based on RFC 2254.
      // RFC 2254 defines certain operators for the filter,including substring
      // matches,equality,approximate match,greater than,less than.These
      // operators are mapped to operators with corresponding semantics in the
      // underlying directory.For example,for the equals operator,suppose
      // the directory has a matching rule defining "equality"of the
      // attributes in the filter.This rule would be used for checking
      // equality of the attributes specified in the filter with the attributes
      // of objects in the directory.Similarly,if the directory has a
      // matching rule for ordering,this rule would be used for
      // making "greater than"and "less than"comparisons.
      String filter = "(&(objectClass=jmxConnector)(jmxServiceURL=*))";

      // Prepare the search controls.
      SearchControls ctrls = new SearchControls();

      // Want to get all jmxConnector objects,wherever they've been
      // registered.
      ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

      // Want to get only the jmxServiceURL (comment this line and
      // all attributes will be returned).
      ctrls.setReturningAttributes(new String[]{"jmxServiceURL"});

      // Search
      final NamingEnumeration results = root.search("", filter, ctrls);

      // Get the URL.
      while (results.hasMore())
      {
        final SearchResult res = (SearchResult)results.nextElement();
        final Attributes attrs = res.getAttributes();
        final Attribute attr = attrs.get("jmxServiceURL");
        final String urlStr = (String)attr.get();

        // Make a connector.
        final JMXServiceURL url = new JMXServiceURL(urlStr);
        final JMXConnector conn =
          JMXConnectorFactory.newJMXConnector(url, null);

        // Start using the connector.
        conn.connect(null);
      }
    }
    catch (NamingException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
