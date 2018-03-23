package ru.protei.portal.ui.documentation.client.common;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.ui.common.client.lang.En_OrganizationCodeLang;

public class DocumentationUtils {

    public static String formatNumber(DecimalNumber number) {
        if (number == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(lang.getName(number.getOrganizationCode()))
                .append(".")
                .append(NumberFormat.getFormat("000000").format(number.getClassifierCode()))
                .append(".")
                .append(NumberFormat.getFormat("000").format(number.getRegisterNumber()));

        if (number.getModification() != null) {
            sb.append("–").append(NumberFormat.getFormat("00").format(number.getModification()));
        }

        return sb.toString();
    }

    @Inject
    static En_OrganizationCodeLang lang;
}
