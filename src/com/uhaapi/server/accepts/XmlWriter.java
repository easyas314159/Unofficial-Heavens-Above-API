package com.uhaapi.server.accepts;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

@Provider
@Produces({MediaType.APPLICATION_XML})
public class XmlWriter implements MessageBodyWriter<Object> {

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

		try {
			JAXBContext context = JAXBContext.newInstance(clazz);

		    Marshaller m = context.createMarshaller();
		    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		    m.marshal(response, output);
		}
		catch(JAXBException ex) {
			throw new IOException(ex);
		}
	}
}
