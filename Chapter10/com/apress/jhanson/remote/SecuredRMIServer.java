package com.apress.jhanson.remote;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.remote.rmi.RMIConnectorServer;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXConnectorServer;
import java.util.HashMap;
import java.io.File;

/**
 * Created by J. Jeffrey Hanson
 * Apress Pro JMX.
 */
public class SecuredRMIServer
{
  public static void main(String[] args)
  {
    try
    {
      // Instantiate the MBean server.
      //
      MBeanServer mbs = MBeanServerFactory.createMBeanServer();
      HashMap env = new HashMap();

      // Provide SSL-based RMI socket factories.
      //
      RMISSLClientSocketFactory csf = new RMISSLClientSocketFactory();
      RMISSLServerSocketFactory ssf = new RMISSLServerSocketFactory();
      env.put(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE,
              csf);
      env.put(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE,
              ssf);

      env.put("jmx.remote.x.password.file",
              "config" + File.separator + "password.properties");

      // Create an RMI connector server.
      //
      JMXServiceURL url =
        new JMXServiceURL(
          "service:jmx:rmi:///jndi/rmi://myhost:9999/server");
      JMXConnectorServer cs =
        JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbs);
      
      //Start the RMI connector server.
      //
      cs.start();
      System.out.println("\nConnector server successfully started...");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
