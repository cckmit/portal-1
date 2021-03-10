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

public class AddingIssueCommentHelpTextConverter implements InitializingBean {

    private static Logger log = LoggerFactory.getLogger(AddingIssueCommentHelpTextConverter.class);

    private final String fileNameRu = "help_ru.html";
    private final String fileNameEn = "help_en.html";

    private String textRu;
    private String textEn;

    @Autowired
    WinterFilePathResolver resolver;

    @Override
    public void afterPropertiesSet() {
       textRu = getHtmlAsString(fileNameRu);
       textEn = getHtmlAsString(fileNameEn);
    }

    private String getHtmlAsString(String fileName) {
        String html = "";
        URL path = ConfigUtils.locateFileOrDirectory(fileName, resolver.resolve(fileName));
        log.info("File path is " + path);
        try (InputStream in = path.openStream()) {
            html = IOUtils.toString(in, Charset.defaultCharset());
            log.info("{} loaded successful from {}", fileName, path);
        } catch (Exception e) {
            log.warn("{} not found", fileName);
        }

        return html;
    }

    public String getText(String locale) {
        return locale.equals("ru") ? textRu : textEn;
    }
}
