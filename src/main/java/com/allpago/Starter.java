package com.allpago;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.allpago.config.AppConfig;
import com.allpago.config.PropUtil;

public class Starter {
	private static int SERVER_PORT = Integer.parseInt(PropUtil.getInstance().getProperty("app.port"));
	private static final String CONTEXT_PATH = "rest";
	private static Server server = null;
	
	public static void main( final String[] args ) throws Exception {
		startServer();
	}
	
	public static int getAppPort(){
		return SERVER_PORT;
	}
	
	public static void startServer()  throws Exception{
		
		Resource.setDefaultUseCaches( false );
		
		server = new Server( SERVER_PORT );		
		System.setProperty( AppConfig.SERVER_PORT, Integer.toString( SERVER_PORT ) );
		System.setProperty( AppConfig.SERVER_HOST, "localhost" );
		System.setProperty( AppConfig.CONTEXT_PATH, CONTEXT_PATH );				

		// Configuring Apache CXF servlet and Spring listener  
		final ServletHolder servletHolder = new ServletHolder( new CXFServlet() ); 		 		
 		final ServletContextHandler context = new ServletContextHandler(); 		
 		context.setContextPath( "/" );
 		context.addServlet( servletHolder, "/" + CONTEXT_PATH + "/*" ); 	 		
 		context.addEventListener( new ContextLoaderListener() ); 		 		
 		context.setInitParameter( "contextClass", AnnotationConfigWebApplicationContext.class.getName() );
 		context.setInitParameter( "contextConfigLocation", AppConfig.class.getName() );
 		
 	    // Configuring Swagger as static web resource
 		final ServletHolder swaggerHolder = new ServletHolder( new DefaultServlet() );
 		final ServletContextHandler swagger = new ServletContextHandler();
 		swagger.setContextPath( "/allpago" );
 		swagger.addServlet( swaggerHolder, "/*" );
        swagger.setResourceBase( new ClassPathResource( "/webapp" ).getURI().toString() );

 		final HandlerList handlers = new HandlerList();
 		handlers.addHandler( context );
 		handlers.addHandler( swagger );
        server.setHandler( handlers );
        server.start();
        server.join();		
	}
	
	public static void stopServer(){
		try {
			if(null != server){
				server.stop();
			}
		} catch (Exception e) {
			System.out.println("Error while stoping server");
			e.printStackTrace();
		}
	}
	
	public static boolean isServerRunning(){
		if(null != server){
			return server.isRunning();
		}
		return false;
	}
}

