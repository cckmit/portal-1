package ru.protei.portal.ui.common.client.activity.attachment;

import com.google.gwt.core.client.GWT;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.Collections;
import java.util.Map;

public class AttachmentLinkProvider {

    public static String getLink(Long attachmentId) {

        String extLink = linkMap.get(attachmentId);
        if (StringUtils.isEmpty(extLink)){
            return null;
        }
        return DOWNLOAD_PATH + extLink;
    }

    public static void setLinkMap(Map<Long, String> linkMap) {
        AttachmentLinkProvider.linkMap = linkMap;
    }

    private static Map<Long, String> linkMap = Collections.emptyMap();

    private static final String DOWNLOAD_PATH = GWT.getModuleBaseURL() + "springApi/files/";
}
