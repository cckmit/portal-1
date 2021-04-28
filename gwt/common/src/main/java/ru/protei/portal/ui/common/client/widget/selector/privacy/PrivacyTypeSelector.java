package ru.protei.portal.ui.common.client.widget.selector.privacy;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.common.PrivacyTypeStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseCommentPrivacyTypeLang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class PrivacyTypeSelector extends ButtonPopupSingleSelector<En_CaseCommentPrivacyType> {

    @Inject
    public void init() {
        setSearchEnabled( false );
        setItemRenderer( value -> value == null ? defaultValue : "<i class='" + makeIcon(value) + "'></i>");
    }

    private String makeIcon(En_CaseCommentPrivacyType value) {
        return PrivacyTypeStyleProvider.getIcon(value);
    }

    @Override
    protected SelectorItem<En_CaseCommentPrivacyType> makeSelectorItem(En_CaseCommentPrivacyType element, String elementHtml) {
        PopupSelectorItem<En_CaseCommentPrivacyType> item = new PopupSelectorItem();
        item.setIcon(makeIcon(element) + " m-r-5");
        item.setName(privacyTypeLang.getName(element));
        item.ensureDebugId(DebugIdsHelper.PRIVACY_TYPE.byType(element.name().toLowerCase()));
        return item;
    }

    @Override
    protected void showValue(En_CaseCommentPrivacyType value) {
        super.showValue(value);
        setTitle(privacyTypeLang.getName(value));
    }

    @Inject
    En_CaseCommentPrivacyTypeLang privacyTypeLang;
}