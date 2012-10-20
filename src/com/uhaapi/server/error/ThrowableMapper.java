package com.uhaapi.server.error;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.uhaapi.server.api.ApiStatus;
import com.uhaapi.server.api.entity.ApiError;

@Provider
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ThrowableMapper implements ExceptionMapper<Throwable> {
	private final Logger log = Logger.getLogger(getClass());

	@Override
	public Response toResponse(Throwable t) {
		ApiError apiError = null;
		if(t instanceof ApiException) {
			apiError = ((ApiException)t).getError();
		}
		else {
			log.error("", t);
			apiError = new ApiError(ApiStatus.INTERNAL_SERVER_ERROR, "Unknown internal error");
		}
		return Response.status(apiError.getCode())
				.entity(apiError)
				.build();
	}
}
