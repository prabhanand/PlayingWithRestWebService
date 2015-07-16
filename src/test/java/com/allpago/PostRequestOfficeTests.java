package com.allpago;

import static org.junit.Assert.*;

import java.sql.Time;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.allpago.db.Query;
import com.jayway.restassured.RestAssured;

import static com.jayway.restassured.RestAssured.*;

public class PostRequestOfficeTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = Starter.getAppPort();
		Query.saveOffice("officeNameForExistsCase", "address",  new Time(9, 0, 0),  new Time(17, 0, 0), -5);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Query.deleteOffice("officeName1");
		Query.deleteOffice("officeNameForExistsCase");
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testCreateOfficeSucess() {
		
		int code = given().
			formParam("officeName", "officeName1").
			formParam("officeAddress", "officeAddress1").
			formParam("openTime", "9:00").
			formParam("closeTime", "17:00").
			formParam("diff", "-5").when().post("/rest/api/office").thenReturn().statusCode();
		 assertEquals(201, code);
	}

	@Test
	public void testCreateOfficeAlreadyExists() {
		
		int code = given().
			formParam("officeName", "officeNameForExistsCase").
			formParam("officeAddress", "officeAddress1").
			formParam("openTime", "9:00").
			formParam("closeTime", "17:00").
			formParam("diff", "-5").when().post("/rest/api/office").thenReturn().statusCode();
		 assertEquals(409, code);
	}
	
	@Test
	public void testCreateOfficeFailureInvalidInputOpenTime() {
		
		int code = given().
			formParam("officeName", "officeName1").
			formParam("officeAddress", "officeAddress1").
			formParam("openTime", "24:00").
			formParam("closeTime", "5:00").
			formParam("diff", "-5").when().post("/rest/api/office").thenReturn().statusCode();
		 assertEquals(412, code);
	}

	@Test
	public void testCreateOfficeFailureInvalidInputCloseTime() {
		
		int code = given().
			formParam("officeName", "officeName1").
			formParam("officeAddress", "officeAddress1").
			formParam("openTime", "12:00").
			formParam("closeTime", "25:00").
			formParam("diff", "-5").when().post("/rest/api/office").thenReturn().statusCode();
		 assertEquals(412, code);
	}
	
	@Test
	public void testCreateOfficeFailureInvalidInputUTCDiffTime() {
		
		int code = given().
			formParam("officeName", "officeName1").
			formParam("officeAddress", "officeAddress1").
			formParam("openTime", "12:00").
			formParam("closeTime", "20:00").
			formParam("diff", "-13").when().post("/rest/api/office").thenReturn().statusCode();
		 assertEquals(412, code);
	}
	
	@Test
	public void testCreateOfficeFailureInvalidInputOfficeName() {
		
		int code = given().
			formParam("officeName", "").
			formParam("officeAddress", "officeAddress1").
			formParam("openTime", "12:00").
			formParam("closeTime", "20:00").
			formParam("diff", "-11").when().post("/rest/api/office").thenReturn().statusCode();
		 assertEquals(412, code);
	}
	
	//TODO: Add test case to check open and close time should not be same, it should have at least some difference
}
