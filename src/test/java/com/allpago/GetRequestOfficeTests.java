package com.allpago;

import static org.junit.Assert.*;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.allpago.db.Query;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.*;

public class GetRequestOfficeTests {

	private static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String UTC = "UTC";
	
	private static String getCurrentUTCTime(){
		final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone(UTC));
		final String utcTime = sdf.format(new Date());
		return utcTime;
	}
	
	private static Date getUTCTime(){
		String strDate = getCurrentUTCTime();
		Date dateToReturn = null;
	    SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
	    try{
	        dateToReturn = (Date)dateFormat.parse(strDate);
	    }catch (ParseException e){
	        e.printStackTrace();
	    }
	    return dateToReturn;
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = Starter.getAppPort();
		
		Query.saveOffice("TestListOffices1", "address1",  new Time(9, 0, 0),  new Time(17, 0, 0), 0);
		Query.saveOffice("TestListOffices2", "address2",  new Time(18, 0, 0),  new Time(2, 0, 0), 0);
		Query.saveOffice("TestListOffices3", "address3",  new Time(0, 0, 0),  new Time(23, 59, 59), 0);
		
		Date utcDate = getUTCTime();
		//add open office at UTC time zone
		
		//start office after 1 hrs before utc
		Date startTimeU = new Date(utcDate.getTime() - TimeUnit.HOURS.toMillis(2));
		//close office after 10 hrs utc
		Date endTimeU = new Date(utcDate.getTime() + TimeUnit.HOURS.toMillis(10));
		Query.saveOffice("TestListOffices4", "address4",  new Time(startTimeU.getHours(), 0, 0),  new Time(endTimeU.getHours(), 0, 0), 0);
		
		//+5 time zone
		Date utcDiffDate = new Date(utcDate.getTime() + TimeUnit.HOURS.toMillis(5));
		//start office after 1 hrs before utc
		Date startTime = new Date(utcDiffDate.getTime() - TimeUnit.HOURS.toMillis(1));
		//close office after 10 hrs utc
		Date endTime = new Date(utcDiffDate.getTime() + TimeUnit.HOURS.toMillis(10));
		Query.saveOffice("TestListOffices5", "address5",  new Time(startTime.getHours(), 0, 0),  new Time(endTime.getHours(), 0, 0), 5);

		//-5 time zone
		Date utcDiffDateN = new Date(utcDate.getTime() - TimeUnit.HOURS.toMillis(5));
		//start office after 5 hrs
		Date startTimeN = new Date(utcDiffDateN.getTime() - TimeUnit.HOURS.toMillis(2));
		//close office after 15 hrs
		Date endTimeN = new Date(utcDiffDateN.getTime() + TimeUnit.HOURS.toMillis(15));
		Query.saveOffice("TestListOffices6", "address6",  new Time(startTimeN.getHours(), 0, 0),  new Time(endTimeN.getHours(), 0, 0), -5);

		//TODO:Need to add other test cases for various timezones, edge cases for time zone etc.
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Query.deleteOffice("TestListOffices1");
		Query.deleteOffice("TestListOffices2");
		Query.deleteOffice("TestListOffices3");
		Query.deleteOffice("TestListOffices4");
		Query.deleteOffice("TestListOffices5");
		Query.deleteOffice("TestListOffices6");
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testListAllOffices() {
		int code  = given().queryParam("criteria", "all").when().get("/rest/api/office").getStatusCode();
		assertEquals(200, code);
	}
	
	@Test
	public void testListAllOfficesContent() {
		Response response  = given().queryParam("criteria", "all").get("/rest/api/office").thenReturn();
		List names = response.body().jsonPath().getList("officeName");
		assertTrue(names.contains("TestListOffices1"));
		assertTrue(names.contains("TestListOffices3"));
		assertTrue(names.contains("TestListOffices4"));
		assertTrue(names.contains("TestListOffices5"));
	}
	
	@Test
	public void testListOpenOfficesContent() {
		Response response  = given().queryParam("criteria", "open").get("/rest/api/office").thenReturn();
		List names = response.body().jsonPath().getList("officeName");
		assertTrue(names.contains("TestListOffices4"));
		assertTrue(names.contains("TestListOffices5"));
		assertTrue(names.contains("TestListOffices6"));
	}
	
	@Test
	public void testListOfficesInvalidCrateria() {
		int code  = given().queryParam("criteria", "test").when().get("/rest/api/office").getStatusCode();
		assertEquals(412, code);
	}
	
	@Test
	public void testGetOfficeDetails(){
		Response response  = given().get("/rest/api/office/TestListOffices1").thenReturn();
		String responseStr = response.asString();
		JsonPath jsonPath = new JsonPath(responseStr);
		String officeName = jsonPath.getString("officeName");
	
		String officeAddress = jsonPath.getString("officeAddress");
		int diff = jsonPath.getInt("diff");
		
		assertEquals(200, response.getStatusCode());
		
		assertTrue(officeName.equals("TestListOffices1"));
		assertTrue(officeAddress.equals("address1"));
		assertTrue(0 == diff);
	}

	@Test
	public void testGetOfficeDetailsNotExistingOffice(){
		Response response  = given().get("/rest/api/office/NOTEXISTINGOFFICE").thenReturn();
		assertEquals(404, response.getStatusCode());
	}

}
