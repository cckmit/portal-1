package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;

public class DocumentNameColumn<T extends Document> extends ClickColumn<T> {
    @Inject
    public DocumentNameColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.setInnerText(lang.documentName());
        columnHeader.addClassName("document-number-column");
    }

    @Override
    public void fillColumnValue(Element cell, T value) {
        StringBuilder html = new StringBuilder();

        if (value.isDeprecatedUnit()) {
            html
                    .append("<div class =\"document-name\">")
                    .append("<i class=\"fa fa-lock m-r-5\" id=\"" + DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_TABLE.LOCK_ICON + "\"></i> ")
                    .append(value.getName())
                    .append("</div>");
        } else {
            html.append( "<div class=\"document-name\">" + value.getName() + "</div>" ) ;
        }

        if (value.getProject() != null && value.getProject().getCustomer() != null) {
            html.append( "<div class=\"document-name\">" + value.getProject().getCustomer().getCname() + "</div>" );
        }
        html.append( "<br/>" );
        html.append( "<b>" + value.getType().getName() + " " + DateFormatter.formatYear(value.getCreated()) + "</b>" );
        html.append( "<br/>" );
        html.append( value.getApproved() ? lang.documentApproved() : lang.documentNotApproved() );
        html.append( "<br/>" );
        html.append( "<small>" + lang.documentCreated(DateFormatter.formatDateOnly(value.getCreated())) + " " + DateFormatter.formatTimeOnly(value.getCreated()) + "</small>" );

        if (value.isDeprecatedUnit()) {
            cell.addClassName("deprecated-entity");
        }
        cell.setInnerHTML(html.toString());
    }

    private Lang lang;
}
