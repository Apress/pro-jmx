package com.apress.jhanson.remote;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.Serializable;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.rmi.server.RMIClientSocketFactory;
import java.security.KeyStore;
import java.net.Socket;

/**
 * Created by J. Jeffrey Hanson
 * Apress Pro JMX.
 */
public class RMISSLClientSocketFactory
  implements RMIClientSocketFactory, Serializable
{
  private transient SSLSocketFactory csf = null;

  public Socket createSocket(String host, int port) throws IOException
  {
    if (csf == null)
    {
      try
      {
        String truststore = "config" + File.separator + "truststore";
        char truststorepass[] = "trustword".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(truststore), truststorepass);
        TrustManagerFactory tmf =
          TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);
        SSLContext ctx = SSLContext.getInstance("TLSv1");
        ctx.init(null, tmf.getTrustManagers(), null);
        csf = ctx.getSocketFactory();
      }
      catch (IOException e)
      {
        throw e;
      }
      catch (Exception e)
      {
        throw (IOException) new IOException().initCause(e);
      }
    }
    return (SSLSocket) csf.createSocket(host, port);
  }
}
