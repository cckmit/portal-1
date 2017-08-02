package ru.protei.portal.hpsm.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamSource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by michael on 07.06.17.
 */
public class StringStreamSource implements InputStreamSource {

    String data;

    public StringStreamSource(String data) {
        this.data = data;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return IOUtils.toInputStream(this.data);
    }
}
