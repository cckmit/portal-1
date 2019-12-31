package ru.protei.portal.core.model.yt.api.value;

import ru.protei.portal.core.model.yt.api.YtDto;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-TextFieldValue.html */
public class YtTextFieldValue extends YtDto {

    public String markdownText;
    public String text;

    @Override
    public String toString() {
        return "YtTextFieldValue{" +
                "markdownText='" + markdownText + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
