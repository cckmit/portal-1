package ru.protei.portal.core.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;

public class ConfigParser {

    public static <T> T parse(String fileName, Class<?>... usedClasses) throws IOException {
        return parse(fileName, false, usedClasses);
    }

    @SuppressWarnings("unchecked")
    public static <T> T parse(String fileName, final boolean ignoreUnknownElements, Class<?>... usedClasses) throws IOException {
        try (InputStream is = ConfigParser.class.getClassLoader().getResourceAsStream(fileName)) {

            if (is == null)
                throw new NoSuchFileException(fileName);

            XStream stream = new XStream(new PureJavaReflectionProvider(), new XppDriver()) {
                @Override
                protected MapperWrapper wrapMapper(MapperWrapper next) {
                    if (!ignoreUnknownElements)
                        return super.wrapMapper(next);

                    return new MapperWrapper(next) {
                        @SuppressWarnings("rawtypes")
						@Override
                        public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                            if (definedIn == Object.class) {
                                return false;
                            }
                            return super.shouldSerializeMember(definedIn, fieldName);
                        }
                    };
                }
            };
            stream.processAnnotations(usedClasses);

            return (T) stream.fromXML(is);
        }
    }


}