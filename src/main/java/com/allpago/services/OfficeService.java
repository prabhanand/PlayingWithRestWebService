package com.allpago.services;

import java.sql.Time;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.allpago.db.Query;
import com.allpago.exceptions.AddOfficeException;
import com.allpago.exceptions.InvalidInputException;
import com.allpago.exceptions.OfficeAlreadyExistsException;
import com.allpago.resource.Office;

@Service
public class OfficeService {
	
	private Logger logger = LoggerFactory.getLogger(OfficeService.class);
	
	private Time validateTime(String tm){
		if(null == tm || tm.isEmpty()){
			return null;
		}

		String t[] = tm.split(":");
		if(t.length != 2){
			return null;
		}
		
		try{
			int hh = Integer.parseInt(t[0]);
			if(hh < 0 || hh > 23){
				return null;
			}

			int ss = Integer.parseInt(t[1]);
			if(ss < 0 || ss > 59){
				return null;
			}
			return new Time(hh, ss,0);
		}catch(NumberFormatException ex){
			logger.error("error in parsing time parameter");
			return null;
		}
	}

	 
	/**
	 * Create a office in the system. Do required validations 
	 * 
	 * @param officeName
	 * @param officeAddress
	 * @param openTime
	 * @param closeTime
	 * @param diff
	 * @return Office for success else error code
	 */
	
	public Office addOffice( final String officeName, final String officeAddress, String openTime, String closeTime, int diff) {
		Time ot = null;
		logger.debug("add office data validation start");
		try{
			ot = validateTime(openTime);
			if(null == ot){
				throw new InvalidInputException(openTime);
			}
		}catch(NumberFormatException ex){
			throw new InvalidInputException(openTime);
		}
		
		Time ct = null;
		try{
			ct = validateTime(closeTime);
			if(null == ct){
				throw new InvalidInputException(openTime);
			}
		}catch(NumberFormatException ex){
			throw new InvalidInputException(openTime);
		}

		try{
			if(diff < -12 || diff > 14){
				throw new InvalidInputException(openTime);
			}
			//TODO:Depends on the how to handle this case, the correct error message can be sent to user
		}catch(NumberFormatException ex){
			throw new InvalidInputException(openTime);
		}
		
		if(null == officeName || officeName.isEmpty()){
			throw new InvalidInputException(officeName);
		}
		
		final Office office = new Office( officeName, officeAddress, ot, ct, diff );
		
		if(office.isExists()){
			throw new OfficeAlreadyExistsException(officeName);
		}
		
		logger.debug("add office data validation done");
		
		if(!office.save()){
			logger.debug("add office failure !!");
			throw new AddOfficeException(officeName);
		}
		
		logger.debug("add office success");
		return office;
	}
	

	
	/**
	 * Get the list of offices from persistence layer
	 * @return
	 */
	public static Collection<Office> getAllOfficeList(){
		return Query.getAllOfficeList();
	}
	
	/**
	 * Get the list of open offices.
	 * 
	 * @return
	 */
	public static Collection<Office> getOpenOfficeList(){
		return Query.getOpenOfficeList();
	}
	
	/**
	 * Get the list of open offices.
	 * 
	 * @return
	 */
	public static Office getOffice(String name){
		return Query.getOffice(name);
	}
}
