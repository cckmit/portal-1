package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_TextMarkup;

import java.io.Serializable;

public class TextWithMarkup implements Serializable {

    private String text;
    private En_TextMarkup textMarkup;

    public TextWithMarkup() {}

    public TextWithMarkup(String text, En_TextMarkup textMarkup) {
        this.text = text;
        this.textMarkup = textMarkup;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public En_TextMarkup getTextMarkup() {
        return textMarkup;
    }

    public void setTextMarkup(En_TextMarkup textMarkup) {
        this.textMarkup = textMarkup;
    }

    @Override
    public String toString() {
        return "TextWithMarkup{" +
                "text='" + text + '\'' +
                ", textMarkup=" + textMarkup +
                '}';
    }
}
