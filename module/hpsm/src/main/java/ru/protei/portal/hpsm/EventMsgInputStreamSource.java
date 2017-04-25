package ru.protei.portal.hpsm;

import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import ru.protei.portal.hpsm.struct.EventMsg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by michael on 19.04.17.
 */
public class EventMsgInputStreamSource implements InputStreamSource {

    @Autowired
    XStream stream;

    EventMsg msg;

    String charset = "utf-8";

    public EventMsgInputStreamSource () {

    }

    public EventMsgInputStreamSource attach (EventMsg msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(asString().getBytes(charset));
    }

    public String asString () {
        return stream.toXML(this.msg);
    }
}
