package ru.protei.portal.config;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.winter.core.utils.config.ConfigUtils;
import ru.protei.winter.core.utils.file.WinterFilePathResolver;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

public class HtmlToStringConverter {

    private static Logger log = LoggerFactory.getLogger(HtmlToStringConverter.class);

    @Autowired
    WinterFilePathResolver resolver;

    public String getHtmlAsString(String fileName) {
        String result = "";
        URL path = ConfigUtils.locateFileOrDirectory(fileName, resolver.resolve(fileName));
        try (InputStream in = path.openStream()) {
            result = IOUtils.toString(in, Charset.defaultCharset());
            log.info("{} loaded successful from {}", fileName, path);
        } catch (Exception e) {
            log.warn("{} not found", fileName);
        }

        return result;
    }
}
