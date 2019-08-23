package ru.protei.portal.core.model.dict;

/**
 * Created by michael on 23.05.16.
 */
public enum En_DevUnitType {
    COMPONENT(1),
    PRODUCT(2),
    DIRECTION(3),
    COMPLEX(4);

    private En_DevUnitType (int typeId) {
        this.id = typeId;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public String getImgSrc() {
        return "./images/du_" + this.name().toLowerCase() + ".png";
    }

    public static En_DevUnitType forId (int id) {
        for (En_DevUnitType it : En_DevUnitType.values())
            if (it.getId() == id)
                return it;

        return null;
    }

    public static En_DevUnitType[] getValidValues() {
        return new En_DevUnitType[] {COMPLEX, PRODUCT, COMPONENT};
    }
}
