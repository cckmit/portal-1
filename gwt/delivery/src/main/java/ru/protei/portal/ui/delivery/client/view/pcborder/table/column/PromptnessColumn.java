package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.Element;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderPromptnessLang;
import ru.protei.portal.ui.common.client.lang.Lang;

public class PromptnessColumn extends ClickColumn<PcbOrder> {

    public PromptnessColumn(Lang lang, En_PcbOrderPromptnessLang promptnessLang) {
        this.lang = lang;
        this.promptnessLang = promptnessLang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.setInnerText(lang.pcbOrderPromptness());
    }

    @Override
    public void fillColumnValue(Element cell, PcbOrder pcbOrder) {
        if (pcbOrder == null) {
            return;
        }

        cell.setInnerHTML(promptnessLang.getName(pcbOrder.getPromptness()));
    }

    Lang lang;
    En_PcbOrderPromptnessLang promptnessLang;
}
