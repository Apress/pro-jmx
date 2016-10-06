package com.apress.jhanson.remote;

import javax.management.remote.JMXConnector;
import java.util.HashMap;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
class MyJNDIComponent
{
  private JMXConnector connector = null;
  private HashMap properties = null;

  public void store(JMXConnector connector, HashMap properties)
  {
    this.connector = connector;
    this.properties = properties;
  }

  public JMXConnector retrieveConnectorClient()
  {
    return connector;
  }

  public HashMap getProperties()
  {
    return properties;
  }
}
