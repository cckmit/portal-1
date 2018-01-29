package ru.protei.portal.core.model.dict;

/**
 *
 */
public enum En_CompanyCategory {

    CUSTOMER(1),
    PARTNER(2),
    SUBCONTRACTOR(3),
    OFFICIAL(4),
    HOME(5);

    En_CompanyCategory( long id ) {
        this.id = id;
    }

    private final long id;

    public long getId() {
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
