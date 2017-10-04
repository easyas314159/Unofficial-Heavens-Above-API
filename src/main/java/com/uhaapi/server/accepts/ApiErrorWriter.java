package com.uhaapi.server.accepts;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;

import com.uhaapi.server.api.entity.ApiError;

@Provider
@Produces({MediaType.TEXT_PLAIN})
public class ApiErrorWriter implements MessageBodyWriter<ApiError> {
	@Override
	public long getSize(ApiError response, Class<?> clazz, Type type, Annotation[] a, MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] a, MediaType mediaType) {
		return ApiError.class.isAssignableFrom(clazz);
	}

	@Override
	public void writeTo(ApiError response, Class<?> clazz, Type type,
			Annotation[] a, MediaType mediaType,
			MultivaluedMap<String, Object> headers, OutputStream output)
			throws IOException, WebApplicationException {

		Writer w = new OutputStreamWriter(output, Charset.forName("US-ASCII"));
		w.write(response.getMessage());
		IOUtils.closeQuietly(w);
	}
}
