package ru.protei.portal.core.model.ent;


import ru.protei.portal.core.model.dict.En_OrganizationCode;

import java.io.Serializable;

/**
 * Оборудование
 */
public class Equipment implements Serializable {
    private Long id;

    private String name;

    private String specificationName;

    private En_OrganizationCode organizationCode;

    private String classifierCode;

    private String registerNumber;


}
