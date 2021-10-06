package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            Map<En_PersonRoleType, StringBuilder> roleToContractors = new HashMap<En_PersonRoleType, StringBuilder>();
            for (PersonProjectMemberView contractor : contractors) {
                En_PersonRoleType role = contractor.getRole();
                if (roleToContractors.get(role) == null) {
                    roleToContractors.put(role, new StringBuilder());
                }

                roleToContractors.get(role).append(contractor.getDisplayShortName()).append(", ");
            }

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<En_PersonRoleType, StringBuilder> entry : roleToContractors.entrySet()) {
                String strContractors = entry.getValue().toString();
                sb.append("<b>").append(personRoleTypeLang.getName(entry.getKey()))
                  .append("</b>: ").append(strContractors, 0, strContractors.length() - 2)
                  .append("<br/>");
            }

            com.google.gwt.dom.client.Element root = DOM.createDiv();
            root.setInnerHTML(sb.toString());
            cell.appendChild(root);
        }
    }

    Lang lang;
    En_PersonRoleTypeLang personRoleTypeLang;
    private static final String CLASS_NAME = "contractors";
}
