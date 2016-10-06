package com.apress.jhanson.remote;

import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServer;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;
import java.util.HashMap;

/**
 * Created by J. Jeffrey Hanson
 * Apress Pro JMX.
 */
public class SecuredGenericServer
{
  public static void main(String[] args)
  {
    try
    {
      // Create the MBean server.
      //
      MBeanServer mbs = MBeanServerFactory.createMBeanServer();
      HashMap env = new HashMap();

      // Initialize the SSLSocketFactory.
      //
      String keystore = "config" + File.separator + "keystore";
      char keystorepass [] = "password".toCharArray();
      char keypassword [] = "password".toCharArray();
      KeyStore ks = KeyStore.getInstance("JKS");
      ks.load(new FileInputStream(keystore), keystorepass);
      KeyManagerFactory kmf =
        KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, keypassword);
      SSLContext ctx = SSLContext.getInstance("TLSv1");
      ctx.init(kmf.getKeyManagers(), null, null);
      SSLSocketFactory ssf = ctx.getSocketFactory();

      // Add SASL/PLAIN mechanism server provider.
      //
      Security.addProvider(new ServerProvider());

      // The profiles supported by this server are TLS and SASL/PLAIN.
      //
      env.put("jmx.remote.profiles", "TLS SASL/PLAIN");

      env.put("jmx.remote.tls.socket.factory", ssf);
      env.put("jmx.remote.tls.enabled.protocols", "TLSv1");
      env.put("jmx.remote.tls.enabled.cipher.suites",
              "SSL_RSA_WITH_NULL_MD5");

      // Callback handler used by the PLAIN SASL server mechanism
      // to perform user authentication.
      //
      env.put("jmx.remote.sasl.callback.handler",
              new PropertiesFileCallbackHandler("config" +
                                                File.separator +
                                                "password.properties"));
      // Create a JMXMP connector server.
      //
      JMXServiceURL url = new JMXServiceURL("jmxmp", null, 5555);
      JMXConnectorServer cs =
        JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbs);

      // Start the JMXMP connector server.
      //
      cs.start();
      System.out.println("\nJMXMP connector server successfully started...");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
