package com.allpago.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class OfficeAlreadyExistsException extends WebApplicationException {
	private static final long serialVersionUID = 6817489620338221395L;

	public OfficeAlreadyExistsException( final String email ) {
		super(
			Response
				.status( Status.CONFLICT )
				.build()
		);
	}
}
