/*
 * Copyright 2015, TopicQuests
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.topicquests.backside.servlet;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.EnumSet;
import java.util.List;
//import java.util.logging.Handler;


import javax.servlet.DispatcherType;
import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.topicquests.backside.servlet.apps.admin.AdminServlet;
import org.topicquests.backside.servlet.apps.administrator.AdministratorServlet;
import org.topicquests.backside.servlet.apps.auth.AuthenticationServlet;
import org.topicquests.backside.servlet.apps.base.BaseServlet;
import org.topicquests.backside.servlet.apps.stat.StaticFileServlet;
import org.topicquests.backside.servlet.apps.upload.UploadServlet;
import org.topicquests.backside.servlet.apps.usr.UserServlet;
import org.topicquests.support.util.LoggingPlatform;
/**
 * @author park
 *
 */
public class Main {
	private LoggingPlatform log = LoggingPlatform.getInstance("logger.properties");
	private ServletEnvironment environment;
	
	/**
	 * @params args
	 * arg[0] = -UI will mean the system boots a topicmap console
	 */
	public Main(String [] args) {
		boolean isConsole = false;
		//check args for -UI
		//NOTE: can add other switches here
		if (args != null && args.length > 0) {
			isConsole = args[0].equals("-UI");
		}
		log.logDebug("MAIN.Booting");
		//////////////////////
		// Setup the environment
		//////////////////////
		try {
			environment = new ServletEnvironment(isConsole);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		File f = new File("");
		String p = f.getAbsolutePath();
		String basePath = p+"/webapps/ROOT/";
		environment.logDebug("MAIN.BASEPATH "+basePath);
		//////////////////////
		// Create the server
		// ideas from http://stackoverflow.com/questions/28190198/cross-origin-filter-with-embedded-jetty
		// and http://www.becodemonkey.com/?p=179
		//////////////////////
		int port = Integer.valueOf(environment.getStringProperty("ServerPort")).intValue();
		Server server = new Server(port);
		environment.logDebug("MAIN.SERVER");
		ServletHandler handler = new ServletHandler();
        FilterHolder holder = new FilterHolder(new CrossOriginFilter());
        holder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        holder.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        holder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        holder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        holder.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
        holder.setName("cross-origin");
        FilterMapping fm = new FilterMapping();
        fm.setFilterName("cross-origin");
        fm.setPathSpec("*");
        handler.addFilter(holder, fm );
        environment.logDebug("MAIN.HANDLER");
      
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addFilter(holder, "/*", EnumSet.of(DispatcherType.REQUEST)); 
        context.setContextPath("/");
		//////////////////////
		// Add servlets, one for each app
        // NOTE that the servlet path parameter is given, e.g. "/*" will grab all, so
        //   it should go last
		//////////////////////
        context.addServlet(new ServletHolder(new AdminServlet(environment, basePath)),"/admin/*");
        context.addServlet(new ServletHolder(new AdministratorServlet(environment, basePath)),"/administrator/*");
        context.addServlet(new ServletHolder(new AuthenticationServlet(environment, basePath)),"/auth/*");
        context.addServlet(new ServletHolder(new UserServlet(environment, basePath)),"/user/*");
//        context.addServlet(new ServletHolder(new TopicMapServlet(environment, basePath)), "/tm/*");
        context.addServlet(new ServletHolder(new StaticFileServlet(environment, basePath)),"/static/*");

        context.addServlet(new ServletHolder(new UploadServlet(environment, basePath)),"/upload/*");
//        context.addServlet(new ServletHolder(new GUIServlet(environment, basePath)),"/gui/*");
        
        /////////////////////////
        // Boot any plugin servlets
        /////////////////////////
        List<?> cps = (List<?>)environment.getProperty("Servlets");
        if (cps != null && !cps.isEmpty()) {
        	int len = cps.size();
        	Servlet s;
        	String classpath, urlFragment;
        	for (int i=0;i<len;i++) {
                        List<?> x = (List<?>)cps.get(i);
        		urlFragment = (String)x.get(0);
        		classpath = (String)x.get(1);
        		try {
        			System.out.println("Main booting: "+urlFragment+" | "+classpath);
        			Class<?> cl = Class.forName(classpath);
        			Constructor<?> co = cl.getConstructor(ServletEnvironment.class, String.class);
        			s = (Servlet)co.newInstance(environment, basePath);
        			context.addServlet(new ServletHolder(s), urlFragment);
        		} catch (Exception e) {
        			environment.logError(e.getMessage(), e);
        			//this is a show stopper
        			throw new RuntimeException(e);
        		}
        	}
        }
        
        //Boot the catchall servlet
        context.addServlet(new ServletHolder(new BaseServlet(environment, basePath)),"/*");
      
        HandlerList handlers = new HandlerList();
        handlers.addHandler(handler);
        handlers.addHandler(context);
        //server.setHandler(handlers);  
        //TODO for some reason, the filterhandler blocked post
        server.setHandler(context);
        environment.logDebug("MAIN.SERVLETS");

		//////////////////////
		// Start the server 
		//////////////////////
        try {
        	environment.logDebug("MAIN.START");

        	server.start();
        	server.join();

        } catch (Exception e) {
        	environment.logError(e.getMessage(), e);
        	e.printStackTrace();
        }
        System.out.println("BacksideServletKS Ready");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main(args);
	}

}
