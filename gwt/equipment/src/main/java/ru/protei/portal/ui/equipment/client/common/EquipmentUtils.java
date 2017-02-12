package ru.protei.portal.ui.equipment.client.common;

import com.google.gwt.dom.builder.shared.ScriptBuilder;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.OrganizationCodeLang;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.dict.En_OrganizationCode;

/**
 * Утилитарный класс классификатора ескд
 */
public class EquipmentUtils {

    public static String formatNumber( DecimalNumber number ) {
        if ( number == null ) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append( lang.getName( number.getOrganizationCode() ) )
                .append( "." )
                .append( number.getClassifierCode() )
                .append( "." )
                .append( number.getRegisterNumber() );

        if ( number.getModification() != null ) {
            sb.append( "–" ).append( number.getModification() );
        }

        return sb.toString();
    }

    @Inject
    static OrganizationCodeLang lang;
}
