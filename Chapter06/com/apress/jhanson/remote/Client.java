package com.apress.jhanson.remote;

import javax.management.remote.JMXConnector;
import java.io.IOException;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class Client
{
  public Client()
  {
  }

  public void connectToSLP()
  {
    MySLPComponent mySLPClientComponent = new MySLPComponent();

    // retrieve the connector client.
    JMXConnector connectorClient = mySLPClientComponent.retrieveConnectorClient();

    try
    {
      // connect
      connectorClient.connect();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void connectToJNDI()
  {
    MySLPComponent mySLPClientComponent = new MySLPComponent();

    // retrieve the connector client.
    JMXConnector connectorClient = mySLPClientComponent.retrieveConnectorClient();

    try
    {
      // connect
      connectorClient.connect();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
