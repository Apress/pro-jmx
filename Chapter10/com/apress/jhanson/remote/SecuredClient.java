package com.apress.jhanson.remote;

import com.sun.jmx.remote.protocol.rmi.ClientProvider;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.util.HashMap;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Provider;
import java.io.FileInputStream;

/**
 * Created by J. Jeffrey Hanson
 * Apress Pro JMX.
 */
public class SecuredClient
{
  public void retrieveMBeanServerConn()
  {
    try
    {
      HashMap env = new HashMap();

      // Initialize the SSLSocketFactory.
      //
      String truststore = "config/truststore";
      char truststorepass [] = "trustword".toCharArray();
      KeyStore ks = KeyStore.getInstance("JKS");
      ks.load(new FileInputStream(truststore), truststorepass);
      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(ks);
      SSLContext ctx = SSLContext.getInstance("TLSv1");
      SecureRandom sr = new SecureRandom();
      sr.nextInt();
      ctx.init(null, tmf.getTrustManagers(), sr);
      SSLSocketFactory ssf = ctx.getSocketFactory();

      // Add SASL/PLAIN mechanism client provider.
      Security.addProvider(MySecurityProvider.getInstance());

      // The profiles required by this client are TLS and SASL/PLAIN.
      env.put("jmx.remote.profiles", "TLS SASL/PLAIN");
      env.put("jmx.remote.tls.socket.factory", ssf);
      env.put("jmx.remote.tls.enabled.protocols", "TLSv1");
      env.put("jmx.remote.tls.enabled.cipher.suites",
              "SSL_RSA_WITH_NULL_MD5");
      env.put("jmx.remote.sasl.callback.handler",
              new UserPasswordCallbackHandler("username", "password"));

      // Create a JMXMP connector client and
      // connect it to the JMXMP connector server.
      JMXServiceURL url = new JMXServiceURL("jmxmp", null, 5555);
      JMXConnector jmxc = JMXConnectorFactory.connect(url, env);

      // Get the MBeanServerConnection.
      MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}

class MySecurityProvider extends Provider
{
  private static MySecurityProvider instance = null;

  public static MySecurityProvider getInstance()
  {
    if (instance == null)
    {
      instance = new MySecurityProvider("MySecurityProvider", 1.0, "Some info");
    }
    return instance;
  }

  protected MySecurityProvider(String name, double version, String info)
  {
    super(name, version, info);
  }
}
