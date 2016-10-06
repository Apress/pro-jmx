package com.apress.jhanson.remote;

import javax.security.auth.callback.*;
import java.io.IOException;

/**
 * Created by J. Jeffrey Hanson
 * Apress Pro JMX.
 */
public class UserPasswordCallbackHandler
  implements CallbackHandler
{
  private String user;
  private char[] pwchars;
  
  public UserPasswordCallbackHandler(String user, String password)
  {
    this.user = user;
    this.pwchars = password.toCharArray();
  }

  public void handle(Callback[] callbacks)
    throws IOException, UnsupportedCallbackException
  {
    for (int i = 0; i < callbacks.length; i++)
    {
      if (callbacks[i] instanceof NameCallback)
      {
        NameCallback ncb = (NameCallback)callbacks[i];
        ncb.setName(user);
      }
      else if (callbacks[i] instanceof PasswordCallback)
      {
        PasswordCallback pcb = (PasswordCallback)callbacks[i];
        pcb.setPassword(pwchars);
      }
      else
      {
        throw new UnsupportedCallbackException(callbacks[i]);
      }
    }
  }

  private void clearPassword()
  {
    if (pwchars != null)
    {
      for (int i = 0; i < pwchars.length; i++)
        pwchars[i] = 0;
      pwchars = null;
    }
  }

  protected void finalize()
  {
    clearPassword();
  }
}
