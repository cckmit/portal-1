package ru.protei.portal.core.model.yt.api.value;

public class TextFieldValue extends FieldValue {
    @Override
    public String getValue() {
        return text;
    }

    public String markdownText;
    public String text;
}
