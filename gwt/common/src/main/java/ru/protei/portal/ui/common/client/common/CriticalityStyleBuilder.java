package ru.protei.portal.ui.common.client.common;

import com.google.gwt.dom.client.Element;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;

/**
 * Билдер css-класса для иконки критичности
 */
public class CriticalityStyleBuilder {

    public static CriticalityStyleBuilder make() {
        return new CriticalityStyleBuilder();
    }

    public Element addClassName ( Element element, En_ImportanceLevel importanceLevel ) {

        switch ( importanceLevel ) {
            case COSMETIC:
                element.addClassName( "icon-cosmetic" );
                break;
            case BASIC:
                element.addClassName( "icon-basic" );
                break;
            case IMPORTANT:
                element.addClassName( "icon-important" );
                break;
            case CRITICAL:
                element.addClassName( "icon-critical" );
                break;
        }

        return element;
    };

    public String getClassName ( En_ImportanceLevel importanceLevel ) {

        switch ( importanceLevel ) {
            case COSMETIC:
                return "icon-cosmetic";
            case BASIC:
                return "icon-basic";
            case IMPORTANT:
                return "icon-important";
            case CRITICAL:
                return "icon-critical";
        }
        return "";
    };

}
