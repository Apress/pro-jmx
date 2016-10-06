package com.apress.jhanson.remote;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnector;
import javax.management.*;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class Agent
{
  private MBeanServer mBeanServer = null;
  private JMXServiceURL url = null;
  private JMXServiceURL serverAddress = null;
  private JMXConnectorServer connectorServer = null;

  public Agent()
  {
    mBeanServer =
      MBeanServerFactory.createMBeanServer();
    try
    {
      url =
        new JMXServiceURL("service:jmx:rmi:///jndi/rmi://myhost:9999/server");
      connectorServer =
        JMXConnectorServerFactory.newJMXConnectorServer(url,
                                                        null,
                                                        mBeanServer);

      // Note that starting ObjectNames with a colon,
      // as in the following statement, implies the default domain
      ObjectName serverName =
        new ObjectName(":type=connectorserver,name=myconnectorserver");
      mBeanServer.registerMBean(connectorServer, serverName);

      connectorServer.start();
      serverAddress = connectorServer.getAddress();
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    catch (MalformedObjectNameException e)
    {
      e.printStackTrace();
    }
    catch (InstanceAlreadyExistsException e)
    {
      e.printStackTrace();
    }
    catch (MBeanRegistrationException e)
    {
      e.printStackTrace();
    }
    catch (NotCompliantMBeanException e)
    {
      e.printStackTrace();
    }
  }

  protected void publishToSLP()
  {
    MySLPComponent mySLPComponent = new MySLPComponent();
    // Publish the server's address using an SLP-enabled component.
    mySLPComponent.publish(serverAddress);
  }

  protected void publishToJNDI()
  {
    try
    {
      HashMap map = new HashMap();
      // add properties to map

      // Obtain the connector stub.
      JMXConnector connectorStub = connectorServer.toJMXConnector(map);

      // Store the stub somewhere in a directory, lookup service, HTTP server, etc.
      MyJNDIComponent myJNDIComponent = new MyJNDIComponent();
      myJNDIComponent.store(connectorStub, map);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
