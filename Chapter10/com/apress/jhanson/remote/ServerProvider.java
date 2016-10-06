package com.apress.jhanson.remote;

/**
 * Created by J. Jeffrey Hanson
 * Apress Pro JMX.
 */
public final class ServerProvider extends java.security.Provider
{
  public ServerProvider()
  {
    super("SaslServerFactory",1.0,"SASL PLAIN SERVER MECHANISM");
    put("SaslServerFactory.PLAIN","ServerFactory");
  }
}