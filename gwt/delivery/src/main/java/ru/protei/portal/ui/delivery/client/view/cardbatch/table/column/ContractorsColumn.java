package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class ContractorsColumn extends ClickColumn<CardBatch> {

    @Inject
    public ContractorsColumn(Lang lang, En_PersonRoleTypeLang roleTypeLang) {
        this.lang = lang;
        this.personRoleTypeLang = roleTypeLang;
        setStopPropogationElementClassName(CLASS_NAME);
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName(CLASS_NAME);
        columnHeader.setInnerText(lang.cardBatchContractors());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {
        if (card == null) {
            return;
        }

        cell.addClassName(CLASS_NAME);

        List<PersonProjectMemberView> contractors = card.getContractors();
        if (contractors != null) {
            StringBuilder sb = new StringBuilder();
            stream(contractors)
                    .collect(Collectors.groupingBy(PersonProjectMemberView::getRole,
                            Collectors.mapping(PersonProjectMemberView::getDisplayName, Collectors.joining(", "))))
                    .forEach((role, team) ->
                            sb.append("<b>")
                                    .append(personRoleTypeLang.getName(role))
                                    .append("</b>: ")
                                    .append(team)
                                    .append("<br/>"));

            com.google.gwt.dom.client.Element root = DOM.createDiv();
            root.setInnerHTML(sb.toString());
            cell.appendChild(root);
        }
    }

    Lang lang;
    En_PersonRoleTypeLang personRoleTypeLang;
    private static final String CLASS_NAME = "contractors";
}
