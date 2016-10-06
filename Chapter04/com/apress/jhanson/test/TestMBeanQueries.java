package com.apress.jhanson.test;

import com.apress.jhanson.agents.ServiceAgent;
import com.apress.jhanson.agents.ServiceAgentException;

import javax.management.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class TestMBeanQueries
{
  private ServiceAgent serviceAgent = null;
  private MBeanServer mbServer = null;

  public static void main(String[] args)
  {
    TestMBeanQueries app = new TestMBeanQueries();
  }

  public TestMBeanQueries()
  {
    serviceAgent = new ServiceAgent();
    mbServer = serviceAgent.getMBeanServer();

    addResources();
    doQuery1();
  }

  private void doQuery1()
  {
    // Create a query expression for attribute "Type"
    // with a value of "Color".
    AttributeValueExp printerTypeAttr = Query.attr("Type");
    StringValueExp type = Query.value("Color");
    QueryExp queryExp1 = Query.match(printerTypeAttr, type);

    // Create a query expression for attribute "Model"
    // with a value of "Acme".
    AttributeValueExp printerModelAttr = Query.attr("Model");
    StringValueExp model = Query.value("Acme");
    QueryExp queryExp2 = Query.match(printerModelAttr, model);

    // Create a query expression for attribute "Location"
    // with a value of "Building B".
    AttributeValueExp printerLocationAttr = Query.attr("Location");
    StringValueExp location = Query.value("Building B");
    QueryExp queryExp3 = Query.match(printerLocationAttr, location);

    // Use the static methods of the Query class to build
    // objects to form conjunctions and disjunctions.
    QueryExp queryExp4 = Query.or(queryExp1, queryExp3);
    QueryExp queryExp5 = Query.or(queryExp2, queryExp3);
    QueryExp queryExp6 = Query.and(queryExp4, queryExp5);

    try
    {
      // Create the ObjectName that specifies the scope
      // of our query.
      ObjectName queryScope = new ObjectName("*:*");

      // Execute the query.
      Set result = mbServer.queryNames(queryScope, queryExp6);

      // Now, iterate over the results of the query.
      Iterator iter = result.iterator();
      while (iter.hasNext())
      {
        Object obj = iter.next();
        System.out.println("Query found:" + obj.toString());
      }
    }
    catch (MalformedObjectNameException e)
    {
      System.err.println("Error:" + e.toString());
    }
  }

  public void doQuery2()
  {
    try
    {
      //Create the ObjectName that specifies the scope
      //of our query.
      ObjectName queryScope = new ObjectName("*:*");

      //Execute the query.
      QueryExp exp = Query.and(Query.geq(Query.attr("Type"),
      Query.value("Color")),
      Query.match(Query.attr("Model"),
      Query.value("Ca*")));

      Set result = mbServer.queryNames(queryScope,exp);
      
      //Now,iterate over the results of the query.
      Iterator iter = result.iterator();
      while (iter.hasNext())
      {
        Object obj =iter.next();
        System.out.println("Query found:" + obj.toString());
      }
    }
    catch (MalformedObjectNameException e)
    {
      System.err.println(e);
    }
    catch (NullPointerException e)
    {
      System.err.println(e);
    }
  }

  protected void addResources()
  {
    try
    {
      //Register printer A.
      String mbeanClassName1 = "com.apress.jhanson.resources.Printer";
      Hashtable properties1 = new Hashtable();
      properties1.put("type", "printer");
      ObjectInstance objInst1 = serviceAgent.addResource("PrinterA",
                                                         properties1,
                                                         mbeanClassName1);

      //Set attributes for printer A.
      mbServer.setAttribute(objInst1.getObjectName(),
                            new Attribute("Type", "BlackAndWhite"));
      mbServer.setAttribute(objInst1.getObjectName(),
                            new Attribute("Model", "HP"));
      mbServer.setAttribute(objInst1.getObjectName(),
                            new Attribute("Location", "Building A"));

      //Register printer B.
      String mbeanClassName2 = "com.apress.jhanson.resources.Printer";
      Hashtable properties2 = new Hashtable();
      properties2.put("type", "printer");
      ObjectInstance objInst2 = serviceAgent.addResource("PrinterB",
                                                         properties2,
                                                         mbeanClassName2);
      //Set attributes for printer B.
      mbServer.setAttribute(objInst2.getObjectName(),
                            new Attribute("Type", "Color"));
      mbServer.setAttribute(objInst2.getObjectName(),
                            new Attribute("Model", "Canon"));
      mbServer.setAttribute(objInst2.getObjectName(),
                            new Attribute("Location", "Building B"));

      //Register printer C.
      String mbeanClassName3 = "com.apress.jhanson.resources.Printer";
      Hashtable properties3 = new Hashtable();
      properties3.put("type", "printer");
      ObjectInstance objInst3 = serviceAgent.addResource("PrinterC",
                                                         properties3,
                                                         mbeanClassName3);

      //Set attributes for printer C.
      mbServer.setAttribute(objInst3.getObjectName(),
                            new Attribute("Type", "BlackAndWhite"));
      mbServer.setAttribute(objInst3.getObjectName(),
                            new Attribute("Model", "Acme"));
      mbServer.setAttribute(objInst3.getObjectName(),
                            new Attribute("Location", "Building B"));
    }
    catch (ServiceAgentException e)
    {
      e.printStackTrace();
    }
    catch (InstanceNotFoundException e)
    {
      System.err.println(e);
    }
    catch (AttributeNotFoundException e)
    {
      System.err.println(e);
    }
    catch (InvalidAttributeValueException e)
    {
      System.err.println(e);
    }
    catch (MBeanException e)
    {
      System.err.println(e);
    }
    catch (ReflectionException e)
    {
      System.err.println(e);
    }
  }
}
