package com.github.olson1998.http.jsoupserial.html;

import com.github.olson1998.http.serialization.ContentDeserializer;
import com.github.olson1998.http.serialization.ResponseMapping;
import com.github.olson1998.http.serialization.context.SerializationContext;
import org.apache.http.entity.ContentType;
import org.jsoup.Jsoup;
import org.jsoup.SerializationException;
import org.jsoup.nodes.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.BiFunction;

import static org.apache.http.entity.ContentType.TEXT_HTML;

public class JsoupHtmlDeserializer implements ContentDeserializer {

    @Override
    public Set<ContentType> getSupportedContentTypes() {
        return Set.of(TEXT_HTML);
    }

    @Override
    public <C> BiFunction<byte[], SerializationContext, C> deserialize(Class<C> deserializedPojoClass) {
        return (htmlBytes, context) -> doDeserializeTextHtml(htmlBytes, context, deserializedPojoClass);
    }

    @Override
    public <C> BiFunction<byte[], SerializationContext, C> deserializeMapped(ResponseMapping<C> responseMapping) {
        return (htmlBytes, context) -> doDeserializeTextHtml(htmlBytes, context, responseMapping.getPojoType());
    }

    private <C> C doDeserializeTextHtml(byte[] htmlBytes, SerializationContext serializationContext, Type type) {
        if (type.equals(Document.class)) {
            return (C) doDeserializeTextHtml(htmlBytes, serializationContext);
        } else {
            throw new SerializationException();
        }
    }

    private Document doDeserializeTextHtml(byte[] htmlBytes, SerializationContext serializationContext) {
        var charset = serializationContext.getContentType().getCharset();
        try (var byteInputStream = new ByteArrayInputStream(htmlBytes)) {
            return Jsoup.parse(byteInputStream, charset.name(), "");
        } catch (IOException e) {
            return null;
        }
    }
}
