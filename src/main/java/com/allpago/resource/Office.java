package com.allpago.resource;

import java.sql.Time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allpago.db.Query;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel( value = "Office", description = "Office resource representation" )
public class Office {
	
	@ApiModelProperty( value = "Office name", required = true ) private String officeName;
	@ApiModelProperty( value = "Office address", required = true ) private String officeAddress;
	@ApiModelProperty( value = "Office Open from", required = true ) private Time openTime;
	@ApiModelProperty( value = "Office Open until", required = true ) private Time closeTime;
	@ApiModelProperty( value = "Office Time difference (UTC)", required = true ) private int diff;
	
	private Logger logger = LoggerFactory.getLogger(Office.class);
	
	public Office(final String officeName, final String officeAddress, Time openTime, Time closeTime, int diff) {
		this.officeName = officeName;
		this.officeAddress =  officeAddress;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.diff = diff;
		//TODO:Display values should be in the same format as the input values
		//Need to add code to generate display value
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}

	
	/**
	 *  Save office object content to database
	 * @return true if saves successfully else false
	 */
	public boolean save(){
		logger.debug("saving office object");
		return Query.saveOffice(officeName, officeAddress, openTime, closeTime, diff);
	}
	
	/**
	 * Check for existence of office 
	 * @return true if office exists else false
	 */
	@JsonIgnore
	public boolean isExists(){
		 return Query.isOfficeExists(officeName);
	}

	public Time getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Time openTime) {
		this.openTime = openTime;
	}

	public Time getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Time closeTime) {
		this.closeTime = closeTime;
	}

	public int getDiff() {
		return diff;
	}

	public void setDiff(int diff) {
		this.diff = diff;
	}
	
	
}
