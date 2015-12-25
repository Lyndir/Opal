package com.lyndir.lhunath.opal.json;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.*;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.OutputSupplier;
import com.google.gson.*;
import com.google.inject.Singleton;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.CharBuffer;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.*;


/**
 * @author lhunath, 2013-10-19
 */
@Provider
@Singleton
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class GsonJsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    private static final Gson gson = new GsonBuilder().serializeNulls()
                                                      .setPrettyPrinting()
                                                      .disableHtmlEscaping()
                                                      .registerTypeHierarchyAdapter( Enum.class, new JsonSerializer<Enum<?>>() {
                                                          @Override
                                                          public JsonElement serialize(final Enum<?> src, final Type typeOfSrc,
                                                                                       final JsonSerializationContext context) {
                                                              return new JsonPrimitive( src.ordinal() );
                                                          }
                                                      } )
                                                      .create();

    public static Gson getGson() {
        return gson;
    }

    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType,
                           final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream)
            throws IOException, WebApplicationException {
        // TODO: Get charset from mediaType
        return gson.fromJson( new InputStreamReader( entityStream, Charsets.UTF_8 ), ifNotNullElse( genericType, type ) );
    }

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(final Object o, final Class<?> type, final Type genericType, final Annotation[] annotations,
                        final MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(final Object o, final Class<?> type, final Type genericType, final Annotation[] annotations,
                        final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream)
            throws IOException, WebApplicationException {
        // TODO: Get charset from mediaType
        entityStream.write( gson.toJson( o, ifNotNullElse( genericType, type ) ).getBytes( Charsets.UTF_8 ) );
    }
}
