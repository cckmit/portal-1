package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by bondarenko on 09.11.16.
 */
public interface ContactItem {

    @JsonIgnore
    String getValue();

    void setValue(String value);

    @JsonIgnore
    String getComment();

    void setComment(String comment);
}
