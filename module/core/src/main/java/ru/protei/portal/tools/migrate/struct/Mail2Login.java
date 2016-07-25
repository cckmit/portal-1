package ru.protei.portal.tools.migrate.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by michael on 25.07.16.
 */
@JsonAutoDetect
public class Mail2Login {

    @JsonProperty("mail")
    public String mail;

    @JsonProperty("uid")
    public String uid;
}
