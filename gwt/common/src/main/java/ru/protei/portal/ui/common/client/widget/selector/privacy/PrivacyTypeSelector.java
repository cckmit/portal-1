package ru.protei.portal.ui.common.client.widget.selector.privacy;

import com.google.gwt.dom.client.Style;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.ui.common.client.common.PrivacyTypeStyleProvider;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class PrivacyTypeSelector extends ButtonPopupSingleSelector<En_CaseCommentPrivacyType> {

    @Inject
    public void init() {
        setSearchEnabled( false );
        addStyleName("privacy-selector");
        setItemRenderer( value -> value == null ? defaultValue : "<i class='" + makeValue(value) + "'></i>");
    }

    private String makeValue(En_CaseCommentPrivacyType value) {
        return PrivacyTypeStyleProvider.getIcon(value);
    }

    @Override
    protected SelectorItem<En_CaseCommentPrivacyType> makeSelectorItem(En_CaseCommentPrivacyType element, String elementHtml ) {
        PopupSelectorItem<En_CaseCommentPrivacyType> item = new PopupSelectorItem();
        item.setIcon( makeValue(element) );
        return item;
    }

    public void setModel(boolean isExtendedPrivacyType) {
        setModel(elementIndex -> {
            List<En_CaseCommentPrivacyType> list;
            if (isExtendedPrivacyType) {
                list = En_CaseCommentPrivacyType.extendPrivacyType();
            } else {
                list = En_CaseCommentPrivacyType.simplePrivacyType();
            }
            if (size(list) <= elementIndex) return null;
            return list.get(elementIndex);
        });
    }
}