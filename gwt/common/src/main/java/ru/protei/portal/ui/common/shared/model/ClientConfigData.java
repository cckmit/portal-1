package ru.protei.portal.ui.common.shared.model;

import java.io.Serializable;

/**
 * Клиентские параметры конфигурации
 */
public class ClientConfigData implements Serializable {

    public String appVersion;
    public String markupHelpLinkMarkdown;
    public String markupHelpLinkJiraMarkup;

    @Override
    public String toString() {
        return "ClientConfigData{" +
                "appVersion='" + appVersion + '\'' +
                ", markupHelpLinkMarkdown='" + markupHelpLinkMarkdown + '\'' +
                ", markupHelpLinkJiraMarkup='" + markupHelpLinkJiraMarkup + '\'' +
                '}';
    }
}
