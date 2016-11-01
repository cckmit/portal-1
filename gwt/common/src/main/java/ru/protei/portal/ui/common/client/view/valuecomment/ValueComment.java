package ru.protei.portal.ui.common.client.view.valuecomment;

/**
 * Пара значение-комментарий
 */
public class ValueComment {

    public ValueComment(){}

    public ValueComment(String value, String comment){
        this.value = value;
        this.comment = comment;
    }

    public String value;
    public String comment;

}
