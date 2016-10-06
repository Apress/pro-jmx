package com.apress.jhanson.remote;

import javax.security.auth.callback.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by J. Jeffrey Hanson
 * Apress Pro JMX.
 */
public final class PropertiesFileCallbackHandler
  implements CallbackHandler
{
  private Properties pwDb;

  public PropertiesFileCallbackHandler(String pwFile)
    throws IOException
  {
    pwDb = new Properties();
    pwDb.load(new FileInputStream(pwFile));
  }

  public void handle(Callback[] callbacks)
    throws UnsupportedCallbackException
  {
    // Retrieve callbacks.
    //
    NameCallback ncb = null;
    PasswordCallback pcb = null;
    for (int i = 0; i < callbacks.length; i++)
    {
      if (callbacks[i] instanceof NameCallback)
      {
        ncb = (NameCallback)callbacks[i];
      }
      else if (callbacks[i] instanceof PasswordCallback)
      {
        pcb = (PasswordCallback)callbacks[i];
      }
      else
      {
        throw new UnsupportedCallbackException(callbacks[i]);
      }
    }

    if (ncb != null && pcb != null)
    {
      String username = ncb.getDefaultName();
      String pw = pwDb.getProperty(username);
      if (pw != null)
      {
        char[] pwchars = pw.toCharArray();
        pcb.setPassword(pwchars);

        // Clear password
        //
        for (int i = 0; i < pwchars.length; i++)
        {
          pwchars[i] = 0;
        }
      }
    }
  }
}

