package com.apress.jhanson.remote;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.security.KeyStore;
import java.net.ServerSocket;

/**
 * Created by J. Jeffrey Hanson
 * Apress Pro JMX.
 */
public class RMISSLServerSocketFactory
  implements RMIServerSocketFactory
{
  private SSLServerSocketFactory ssf = null;

  public ServerSocket createServerSocket(int port) throws IOException
  {
    if (ssf == null)
    {
      try
      {
        String keystore = "config" + File.separator + "keystore";
        char keystorepass[] = "password".toCharArray();
        char keypassword[] = "password".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keystore), keystorepass);
        KeyManagerFactory kmf =
          KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, keypassword);
        SSLContext ctx = SSLContext.getInstance("TLSv1");
        ctx.init(kmf.getKeyManagers(), null, null);
        ssf = ctx.getServerSocketFactory();
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
    
    return (SSLServerSocket) ssf.createServerSocket(port);
  }
}
