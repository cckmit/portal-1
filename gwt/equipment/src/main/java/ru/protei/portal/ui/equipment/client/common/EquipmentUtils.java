package ru.protei.portal.ui.equipment.client.common;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.OrganizationCodeLang;
import ru.protei.portal.ui.common.shared.model.DecimalNumber;
import ru.protei.portal.ui.common.shared.model.OrganizationCode;

/**
 * Утилитарный класс классификатора ескд
 */
public class EquipmentUtils {

    public static String getESKDClassTypeIcon( String classifierCode ) {
        if ( classifierCode == null || classifierCode.isEmpty() || classifierCode.length() < 6 ) {
            return "";
        }

        Integer classType = Integer.parseInt( classifierCode.substring( 0, 2 ) );
        if ( classType >= 71 && classType <= 76 ) {
            return UiConstants.ESKDClassTypeIcons.DETAIL;
        } else {
            return UiConstants.ESKDClassTypeIcons.PRODUCT;
        }
    }

    public static DecimalNumber getDecimalNumberByStringValues( OrganizationCode code, String classifierCode, String regNum ) {
        String number = regNum;
        String modification = null;
        if ( number.contains( "-" ) ) {
            String[] num = regNum.split( "-" );
            number = num[0];
            modification = num[1];
        }

        return new DecimalNumber( code, classifierCode, number, modification );
    }

    public static String formatNumberByStringValues( OrganizationCode code, String classifierCode, String regNum ) {
        if ( code == null || classifierCode == null ) {
            return "";
        }

        return lang.getName( code ) + "." + classifierCode + "." + regNum;
    }

    @Inject
    static OrganizationCodeLang lang;
}
