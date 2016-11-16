package ru.protei.portal.ui.common.client.widget.validatefield;


import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.TextAreaElement;

/**
 * TextArea c возможностью валидации
 */
public class ValidableTextArea extends ValidableTextBoxBase{

    public ValidableTextArea(){
        super(Document.get().createTextAreaElement());
//        this.setStyleName("gwt-TextArea");
    }

    public int getCharacterWidth() {
        return this.getTextAreaElement().getCols();
    }

    public int getCursorPos() {
        return this.getImpl().getTextAreaCursorPos(this.getElement());
    }

    public int getSelectionLength() {
        return this.getImpl().getTextAreaSelectionLength(this.getElement());
    }

    public int getVisibleLines() {
        return this.getTextAreaElement().getRows();
    }

    public void setCharacterWidth(int width) {
        this.getTextAreaElement().setCols(width);
    }

    public void setVisibleLines(int lines) {
        this.getTextAreaElement().setRows(lines);
    }

    private TextAreaElement getTextAreaElement() {
        return (TextAreaElement)this.getElement().cast();
    }
}
