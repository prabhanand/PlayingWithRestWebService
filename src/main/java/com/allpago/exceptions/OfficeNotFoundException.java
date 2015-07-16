package com.allpago.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class OfficeNotFoundException extends WebApplicationException {
	private static final long serialVersionUID = -2894269137259898072L;
	
	public OfficeNotFoundException( final String name ) {
		super(
			Response
				.status( Status.NOT_FOUND )
				.build()
		);
	}
}
