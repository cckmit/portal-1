package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

/**
 * Тип местоположения
 */
public enum En_LocationType implements HasId {
    DISTRICT( 1 ),
    REGION( 2 ),
    MUNICIPALITY( 3 );

    private En_LocationType( int typeId ) {
        this.id = typeId;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static En_LocationType forId (int id) {
        for (En_LocationType it : En_LocationType.values())
            if (it.getId() == id)
                return it;

        return null;
    }
}
