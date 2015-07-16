package com.allpago.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class AddOfficeException extends  WebApplicationException{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddOfficeException( final String name ) {
		super(
			Response
				.status( Status.METHOD_NOT_ALLOWED )
				.build()
		);
	}
}
