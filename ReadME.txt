1. Environment used
	a. Java 8
		Here is output of version command
		
		E:\AllpagoExercise>java -version
		java version "1.8.0_45"
		Java(TM) SE Runtime Environment (build 1.8.0_45-b14)
		Java HotSpot(TM) 64-Bit Server VM (build 25.45-b02, mixed mode)
		
	b. RDBMS used - Mariadb10.0.16 (Its just another flavor of mysql)
	   Downlaod link for all platforms is
	   https://downloads.mariadb.org/mariadb/10.0.16/
	   
	   The development is done on windows platform, I used execuatble from below link
	   https://downloads.mariadb.org/interstitial/mariadb-10.0.16/winx64-packages/mariadb-10.0.16-winx64.msi/from/http%3A//archive.mariadb.org/
	   
	c. Build tool used - Maven 3.1.2
		Here is output of version command 
		
		E:\AllpagoExercise>mvn -version
		Apache Maven 3.1.1 (0728685237757ffbf44136acec0402957f723d9a; 2013-09-17 20:52:22+0530)
		Maven home: E:\apache-maven-3.1.1\bin\..
		Java version: 1.8.0_31, vendor: Oracle Corporation
		Java home: C:\Program Files\Java\jdk1.8.0_31\jre
		Default locale: en_US, platform encoding: Cp1252
		OS name: "windows 7", version: "6.1", arch: "amd64", family: "dos"
		
	d. Rest of the libraries are mentioned in pom.xml file   
	   

2. How to build?
	The below mentioned STEPS *MUST* to work this application correctly.
	
	a. Make sure you have downloaded Java and Maven binaries and installed.  
	
	b. Make sure you are connlected to internet, maven is pointing to global repositery, 
	   all required files are downloaded from global repositery at build time
	   
	c. Download correct Mariadb version from mentioned link above and install 
	
	d. In Mariadb create User for with all privileges. This user credentials will be used by application developed.
	
	e. Naviagate to <INSTALLEDFOLDER>\src\main\resources, Find allpago_schema.sql file and execute this file on MariaDB sql prompt.
	   This step will create the required schema in mariadb for this application. 
	 
	   Note: INSTALLEDFOLDER is the folder where you unziped this content
		
	f. Naviagate to <INSTALLEDFOLDER>\src\main\resources and open config.properties file. Edit the file
		i) app.port -> PORT on which this application will start. Default is 8080. If this port is not free please change this as per your need
		i) db.url -> change this to host name and port where database is installed. If it is local host and default port then no change is needed
		ii) db.user -> name of the database user created as part of step 'd' 
		iii) db.password -> database user password
		
	Now we are ready to build !!!
	
	g. Naviagte to <INSTALLEDFOLDER> and depends what kind of build you would like to produce, fire respective command from below 
	
		i) To produce clean build with all test case execution result
		
			mvn clean -Dtest=AllTests package
			
			Note: This command needs DB up and running also It starts server on port mentioned "app.port" property in config.properties file  
			and so that port has to be free
			
		ii) To produce clean build WITHOUT unit test,
			
			mvn clean -Dmaven.test.skip=true package
			
	f. Ready to run !!!
		
		Just fire the below command on command prompt
		
		java -jar target/AllpagoExercise-0.0.1-SNAPSHOT.jar
		
	g. Once server is running, please post the below URL in the browser, 
	   http://localhost:8080/allpago  OR http://<host>:<port>/allpago
	   
	   Click on "office" to expand this link to see API's supprted
	   Click on each API to find how to use and its documentation. 
	   Also you can try form this browser.
	   
	   Also pleaese refere API Docoumentation.pdf in <INSTALLFOLDER> for more documentation. 
		
3. TODO List
	a. add more log at various levels INFO, DEBUG, ERROR
	b. add more unit test cases covering edge cases and time zone related cases.
	c. write instrumentation to capture performance related matrix
	e. using depracated APIs for time, need to find alternative API's for the same
	f. It is assumed that the UTC time difference is in integer, where as time diffrence can be +5.5
	   So need to change data type of UTF time difference to float and handle cases around that
	
			
	   
		
		
	