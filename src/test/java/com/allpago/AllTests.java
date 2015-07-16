package com.allpago;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({DBTest.class, PostRequestOfficeTests.class, GetRequestOfficeTests.class})
public class AllTests {

	@BeforeClass
	public static void setUp() {
		
		System.out.println("Starting webservice server..");
		System.out.println("************************************************************************************************************************");
		System.out.println("*********** Starting with Test execution now, Starting Server !!! ...... ***********************************************");
		System.out.println("************************************************************************************************************************");
		
		try {
			
			new Thread(){
			    public void run() {
			    	try {
						Starter.startServer();
					} catch (Exception e) {
						System.out.println("Error while starting server");
						e.printStackTrace();
						System.exit(-1);
					}
			    }
			}.start();
			
			while(!Starter.isServerRunning()){
				System.out.println("Server is not started so sleeping for 100 milliseconds");
				Thread.sleep(100);
			}
			
			System.out.println("Server Started succesfully");
			
		} catch (Exception e) {
			System.out.println("Error while starting server");
			e.printStackTrace();
			System.exit(-1);
		}
		
	}

	@AfterClass
	public static void tearDown() {
		Starter.stopServer();
		System.out.println("************************************************************************************************************************");
		System.out.println("*********** Done with  Test execution now, Server Stopped !!! . Below see the Test Results ..... ***********************");
		System.out.println("************************************************************************************************************************");
		
	}
}
