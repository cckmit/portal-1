package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

/**
 *
 */
public enum En_CompanyCategory implements HasId {

    CUSTOMER(1),
    PARTNER(2),
    SUBCONTRACTOR(3),
    OFFICIAL(4),
    HOME(5);

    En_CompanyCategory( int id ) {
        this.id = id;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static En_CompanyCategory findById( long id ) {
        for ( En_CompanyCategory value : En_CompanyCategory.values() ) {
            if ( value.getId() == id ) {
                return value;
            }
        }

        return null;
    }
}
