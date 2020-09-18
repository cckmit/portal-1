package ru.protei.portal.config;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.winter.core.utils.config.ConfigUtils;
import ru.protei.winter.core.utils.file.WinterFilePathResolver;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

public class ExternalLinksHtml implements InitializingBean {

    private static Logger log = LoggerFactory.getLogger(ExternalLinksHtml.class);

    private static final String fileName = "links.html";

    private String html;

    @Autowired
    WinterFilePathResolver resolver;

    @Override
    public void afterPropertiesSet() throws Exception {
        URL path = ConfigUtils.locateFileOrDirectory(fileName, resolver.resolve(fileName));
        try (InputStream in = path.openStream()) {
            html = IOUtils.toString(in, Charset.defaultCharset());
            log.info("{} loaded successful from {}", fileName, path);
        } catch (Exception e) {
            log.warn("{} not found", fileName);
        }
    }

    public String getHtml() {
        return html;
    }
}
