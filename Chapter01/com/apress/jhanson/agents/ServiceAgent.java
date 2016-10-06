package com.apress.jhanson.agents;

import com.apress.jhanson.services.*;
import com.sun.jdmk.comm.*;

import javax.management.*;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class ServiceAgent
{
  public static void main(String[] args)
  {
    try
    {
      MBeanServer mbServer =
        MBeanServerFactory.createMBeanServer();
      ObjectName oName =
        new ObjectName("services:name=DateTime,type=information");
      mbServer.registerMBean(new DateTimeService(), oName);
      ObjectName adaptorOName =
        new ObjectName("adaptors:protocol=HTTP");
      HtmlAdaptorServer htmlAdaptor = new HtmlAdaptorServer();
      mbServer.registerMBean(htmlAdaptor, adaptorOName);
      htmlAdaptor.start();
    }
    catch (MBeanRegistrationException e)
    {
      e.printStackTrace();
    }
    catch (NotCompliantMBeanException e)
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
  }
}
