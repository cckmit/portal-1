package ru.protei.portal.ui.common.client.common;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.ui.common.client.lang.En_OrganizationCodeLang;

public class DecimalNumberFormatter {

    public static String formatNumber(DecimalNumber number) {
        if (number == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        appendNumberOrganizationCode(sb, number);
        appendNumberClassifierCode(sb, number);
        appendNumberRegisterNumber(sb, number);
        appendNumberModification(sb, number);
        return sb.toString();
    }

    public static String formatNumberWithoutModification(DecimalNumber number) {
        if (number == null || number.getClassifierCode() == null || number.getRegisterNumber() == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        appendNumberOrganizationCode(sb, number);
        appendNumberClassifierCode(sb, number);
        appendNumberRegisterNumber(sb, number);
        return sb.toString();
    }

    private static void appendNumberOrganizationCode(StringBuilder sb, DecimalNumber number) {
        sb.append(lang.getName(number.getOrganizationCode()));
    }

    private static void appendNumberClassifierCode(StringBuilder sb, DecimalNumber number) {
        if (number.getClassifierCode() == null) {
            return;
        }
        sb.append(".").append(NumberFormat.getFormat("000000").format(number.getClassifierCode()));
    }

    private static void appendNumberRegisterNumber(StringBuilder sb, DecimalNumber number) {
        if (number.getRegisterNumber() == null) {
            return;
        }
        sb.append(".").append(NumberFormat.getFormat("000").format(number.getRegisterNumber()));
    }

    private static void appendNumberModification(StringBuilder sb, DecimalNumber number) {
        if (number.getModification() == null) {
            return;
        }
        sb.append("–").append(NumberFormat.getFormat("00").format(number.getModification()));
    }

    @Inject
    static En_OrganizationCodeLang lang;
}
