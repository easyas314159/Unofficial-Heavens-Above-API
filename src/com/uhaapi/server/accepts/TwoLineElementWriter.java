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
import org.space_track.TwoLineElement;

@Provider
@Produces({MediaType.TEXT_PLAIN})
public class TwoLineElementWriter implements MessageBodyWriter<TwoLineElement> {
	@Override
	public long getSize(TwoLineElement response, Class<?> clazz, Type type, Annotation[] a, MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] a, MediaType mediaType) {
		return TwoLineElement.class.isAssignableFrom(clazz);
	}

	@Override
	public void writeTo(TwoLineElement response, Class<?> clazz, Type type,
			Annotation[] a, MediaType mediaType,
			MultivaluedMap<String, Object> headers, OutputStream output)
			throws IOException, WebApplicationException {

		Writer w = new OutputStreamWriter(output, Charset.forName("US-ASCII"));
		w.write(response.toString());
		IOUtils.closeQuietly(w);
	}
}
