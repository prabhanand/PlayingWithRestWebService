package com.allpago.rs;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.allpago.exceptions.InvalidInputException;
import com.allpago.exceptions.OfficeNotFoundException;
import com.allpago.resource.Office;
import com.allpago.services.OfficeService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path( "/office" ) 
@Api( value = "/office", description = "Manage offices" )

public class OfficeRestService {
	
	Logger logger = LoggerFactory.getLogger(OfficeRestService.class);
			
	@Inject private OfficeService officeService;
	
	@Produces( { MediaType.APPLICATION_JSON } )
	@ApiResponses( {
		@ApiResponse( code = 200, message = "Successfully retrived list of offices." ),
	    @ApiResponse( code = 412, message = "Such criteria doesn't exists. Please see the documentation for more details." )			 
	} )
	@GET
	@ApiOperation( value = "List offices based on input criteria.", notes = "List offices using given criteria. Supported criteria values are "
			+ "<br> a) all - To Lists all the offices <br> b) open - To List all the open offices with respect to UTC time.", response =  Office.class, responseContainer = "List")
	
	public Response getAllOffices(  @ApiParam( value = "Criteria to fetch list of offices, please provide a) all or b) open ", required = true ) @QueryParam( "criteria") @DefaultValue( "all") final String criteria ) {
		 
		if("all".equalsIgnoreCase(criteria)){
			 return Response.status(Response.Status.OK).entity(OfficeService.getAllOfficeList()).build();
		 }else if("open".equalsIgnoreCase(criteria)){
			 return Response.status(Response.Status.OK).entity(OfficeService.getOpenOfficeList()).build();
		 }else{
			 throw new InvalidInputException(criteria);
		 }
	}
	

	@Produces( { MediaType.APPLICATION_JSON } )
	@Path( "/{name}" )
	@GET
	@ApiOperation( value = "Details of office by name", notes = "Details of office by name", response = Office.class )
	@ApiResponses( {
		@ApiResponse( code = 200, message = "Successfull office get call" ),
	    @ApiResponse( code = 404, message = "office with such name doesn't exists" ),
	    @ApiResponse( code = 412, message = "name is null or empty" )
	} )
	public Response getOfficeWithDetails( @ApiParam( value = "Office name to lookup for", required = true ) @PathParam("name") final String name ) {
		
		if(null == name || name.isEmpty()){
			throw new InvalidInputException(name);
		}
		
		Office office = OfficeService.getOffice(name);
		if(null == office){
			throw new OfficeNotFoundException(name);
		}
		return Response.status(Response.Status.OK).entity(office).build();
	}

	@Produces( { MediaType.APPLICATION_JSON  } )
	@POST
	@ApiOperation( value = "Create new office", notes = "Create new office" )
	@ApiResponses( {
	    @ApiResponse( code = 201, message = "Office created successfully" ),
	    @ApiResponse( code = 409, message = "Office with such Name already exists" ),
	    @ApiResponse( code = 412, message = "Invalid input" )
	} )
	public Response createOffice( @Context final UriInfo uriInfo,
			@ApiParam( value = "Office Name", required = true ) @FormParam( "officeName" ) final String officeName, 
			@ApiParam( value = "Office address", required = true ) @FormParam( "officeAddress" ) final String officeAddress, 
			@ApiParam( value = "Office Open from  (Local Time, Valid values -> 0:00 to 23:59, e.g 10:00 is a valid value whereas 10 is invalid value )", required = true ) @FormParam( "openTime" ) final String openTime,
			@ApiParam( value = "Office Open Until (Local Time, Valid values -> 0:00 to 23:59, e.g 10:00 is a valid value whereas 10 is invalid value)", required = true ) @FormParam( "closeTime" ) final String closeTime,
			@ApiParam( value = "Office Time difference with respect to UTC time, Valid values -12 to 14", required = true ) @FormParam( "diff" ) final int diff ) {
		logger.info("Request received to create office");
		officeService.addOffice( officeName, officeAddress, openTime, closeTime, diff);
		logger.info("Done with create office call");
		return Response.created( uriInfo.getRequestUriBuilder().path( officeName ).build() ).build();
	}
		
}
