package ru.protei.portal.core.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Пара значение-комментарий
 */
public class ValueComment implements Serializable{

    public ValueComment(){
        this.value = "";
        this.comment = "";
    }

    public ValueComment(String value, String comment){
        this.value = value;
        this.comment = comment;
    }

    @JsonProperty(value = "v")
    public String value;

    @JsonProperty(value = "c")
    public String comment;

}
