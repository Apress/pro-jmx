package com.apress.jhanson.remote;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnector;
import java.io.IOException;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
class MySLPComponent
{
  private JMXServiceURL serverAddress = null;

  public void publish(JMXServiceURL serverAddress)
  {
    this.serverAddress = serverAddress;
  }

  public JMXConnector retrieveConnectorClient()
  {
    JMXConnector connectorClient = null;

    try
    {
      // Connect to the server using this address.
      connectorClient = JMXConnectorFactory.connect(serverAddress);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return connectorClient;
  }
}