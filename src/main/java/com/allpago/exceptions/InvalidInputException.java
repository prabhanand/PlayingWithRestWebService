package com.allpago.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class InvalidInputException extends  WebApplicationException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidInputException(String param) {
		super(
			Response
				.status( Status.PRECONDITION_FAILED )
				.build()
		);
	}
}
