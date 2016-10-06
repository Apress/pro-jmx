package com.apress.jhanson.agents;

import javax.management.MBeanServer;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.relation.RoleInfo;
import javax.management.relation.Role;
import java.util.ArrayList;

/**
 * Created by J. Jeffrey Hanson
 * Copyright 2004 by J. Jeffrey Hanson - all rights reserved.
 */
public class HRRelationsHelper
{
  private MBeanServer mbServer = null;
  private ServiceAgent agent = null;

  public HRRelationsHelper(ServiceAgent agent)
  {
    this.agent = agent;
    this.mbServer = agent.getMBeanServer();
  }

  public void define()
    throws MBeanException
  {
    createMBeans();
    RoleInfo[] roleInfo = createRoleInfos();
    ArrayList roleList = createRoles();
    createRelationType(roleInfo);
    createRelation(roleList);
  }

  public void createMBeans()
    throws MBeanException
  {
    try
    {
      //Register the worker.
      Object[] params = new Object[3];
      params[0] = new String("John");
      params[1] = new String("Doe");
      params[2] = new Integer(1); //Employee number

      String[] signature = new String[3];
      signature[0] = String.class.getName();
      signature[1] = String.class.getName();
      signature[2] = int.class.getName();

      ObjectName workerOName =
        new ObjectName(agent.getRelationServiceDomain()
                       + ":type=worker,empNum=1");
      mbServer.createMBean("com.apress.jhanson.hr.Worker",
                           workerOName, params, signature);

      //Register the supervisor.
      Object[] params1 = new Object[3];
      params1[0] = new String("Jane");
      params1[1] = new String("Smith");
      params1[2] = new Integer(2);  //Employee number

      String[] signature1 = new String[3];
      signature1[0] = String.class.getName();
      signature1[1] = String.class.getName();
      signature1[2] = int.class.getName();

      ObjectName supervisorOName =
        new ObjectName(agent.getRelationServiceDomain()
                       + ":type=supervisor,empNum=2");
      mbServer.createMBean("com.apress.jhanson.hr.Supervisor",
                           supervisorOName, params1, signature1);

      //Register the work location.
      Object[] params2 = new Object[3];
      params2[0] = new String("D"); //Building name
      params2[1] = new String("D100");  //Mail stop

      String[] signature2 = new String[3];
      signature2[0] = String.class.getName();
      signature2[1] = String.class.getName();

      ObjectName workLocationOName =
        new ObjectName(agent.getRelationServiceDomain()
                       + ":type=worklocation");
      mbServer.createMBean("com.apress.jhanson.hr.WorkLocation",
                           workLocationOName, params2, signature2);
    }
    catch (Exception e)
    {
      throw new MBeanException(e);
    }
  }

  public RoleInfo[] createRoleInfos()
    throws MBeanException
  {
    RoleInfo[] roleInfos = new RoleInfo[3];
    try
    {
      roleInfos[0] = new RoleInfo("Worker",
                                  "com.apress.jhanson.hr.Worker",
                                  true, //Readable
                                  true, //Writable
                                  1, //Must have at least one
                                  100, //Can have 100,max
                                  "Worker role");

      roleInfos[1] = new RoleInfo("Supervisor",
                                  "com.apress.jhanson.hr.Supervisor",
                                  true, //Readable
                                  true, //Writable
                                  1, //Must have at least one
                                  1, //Can have 1,max
                                  "Supervisor role");

      roleInfos[2] = new RoleInfo("WorkLocation",
                                  "com.apress.jhanson.hr.WorkLocation",
                                  true, //Readable
                                  true, //Writable
                                  1, //Must have at least one
                                  1, //Can have 1,max
                                  "WorkLocation role");
    }
    catch (Exception e)
    {
      throw new MBeanException(e);
    }
    return roleInfos;
  }

  public void createRelationType(RoleInfo[] roleInfos)
    throws MBeanException
  {
    try
    {
      Object[] params = new Object[2];
      params[0] = "HRRelationType";
      params[1] = roleInfos;
      String[] signature = new String[2];
      signature[0] = "java.lang.String";
      signature[1] = (roleInfos.getClass()).getName();
      mbServer.invoke(agent.getRelationServiceOName(),
                      "createRelationType", params, signature);
    }
    catch (Exception e)
    {
      throw new MBeanException(e);
    }
  }

  public ArrayList createRoles()
    throws MBeanException
  {
    try
    {
      ArrayList employeeRoleValue = new ArrayList();
      employeeRoleValue.add(new ObjectName(agent.getRelationServiceDomain()
                                           + ":name=worker,empNum=1"));
      employeeRoleValue.add(new ObjectName(agent.getRelationServiceDomain()
                                           + ":name=worker,empNum=2"));
      employeeRoleValue.add(new ObjectName(agent.getRelationServiceDomain()
                                           + ":name=worker,empNum=3"));
      Role employeeRole = new Role("Worker", employeeRoleValue);

      ArrayList supervisorRoleValue = new ArrayList();
      supervisorRoleValue.add(new ObjectName(agent.getRelationServiceDomain()
                                             + ":name=supervisor"));
      Role managerRole = new Role("Supervisor", supervisorRoleValue);

      ArrayList workLocationRoleValue = new ArrayList();
      workLocationRoleValue.add(new ObjectName(agent.getRelationServiceDomain()
                                               + ":name=worklocation"));
      Role physLocationRole = new Role("WorkLocation",
                                       workLocationRoleValue);

      ArrayList roleList = new ArrayList();
      roleList.add(employeeRole);
      roleList.add(managerRole);
      roleList.add(physLocationRole);
      return roleList;
    }
    catch (Exception e)
    {
      throw new MBeanException(e);
    }
  }

  public void createRelation(ArrayList roleList)
    throws MBeanException
  {
    try
    {
      Object[] params = new Object[4];
      params[0] = "HRRelation";
      params[1] = agent.getRelationServiceOName();
      params[2] = "HRRelationType";
      params[3] = roleList;

      String[] signature = new String[4];
      signature[0] = "java.lang.String";
      signature[1] = agent.getRelationServiceOName().getClass().getName();
      signature[2] = "java.lang.String";
      signature[3] = roleList.getClass().getName();
      
      ObjectName relationMBeanName =
        new ObjectName(agent.getRelationServiceDomain() + ":type=RelationMBean");
      mbServer.createMBean("com.apress.jhanson.hr.HRRelation",
                           relationMBeanName,
                           params, signature);

      //Add the relation.
      params = new Object[1];
      signature = new String[1];
      params[0] = relationMBeanName;
      signature[0] = "javax.management.ObjectName";

      mbServer.invoke(agent.getRelationServiceOName(),
                      "addRelation", params, signature);
    }
    catch (Exception e)
    {
      throw new MBeanException(e);
    }
  }
}
