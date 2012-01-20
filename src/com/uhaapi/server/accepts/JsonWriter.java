package com.uhaapi.server.accepts;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

@Provider
@Produces({MediaType.APPLICATION_JSON})
public class JsonWriter implements MessageBodyWriter<Object> {
	private final GsonBuilder gsonBuilder;

	@Inject
	public JsonWriter(GsonBuilder gsonBuilder) {
		this.gsonBuilder = gsonBuilder;
	}

	@Override
	public long getSize(Object response, Class<?> clazz, Type type, Annotation[] a, MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] a, MediaType mediaType) {
		return Object.class.isAssignableFrom(clazz);
	}

	@Override
	public void writeTo(Object response, Class<?> clazz, Type type,
			Annotation[] a, MediaType mediaType,
			MultivaluedMap<String, Object> headers, OutputStream output)
			throws IOException, WebApplicationException {

		Gson gson = gsonBuilder.create();
		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		gson.toJson(response, type, writer);
		IOUtils.closeQuietly(writer);
	}

}
