package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_SocialNetwork;

import java.io.Serializable;

/**
 * Created by michael on 07.11.16.
 */
public class SocialNetworkLink implements Serializable {

    public En_SocialNetwork type;
    public String address;

    SocialNetworkLink () {

    }

}
