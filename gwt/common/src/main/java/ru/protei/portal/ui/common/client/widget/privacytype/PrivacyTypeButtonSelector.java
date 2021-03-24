package ru.protei.portal.ui.common.client.widget.privacytype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.ui.common.client.common.PrivacyTypeStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseCommentPrivacyTypeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

import java.util.List;

public class PrivacyTypeButtonSelector extends ToggleBtnGroup<En_CaseCommentPrivacyType> {
    @Inject
    public void init() {
        fillOptions(En_CaseCommentPrivacyType.simplePrivacyType());
    }

    public void fillOptions(List<En_CaseCommentPrivacyType> list) {
        clear();
        list.forEach(type -> addButton(type));
    }

    private void addButton(En_CaseCommentPrivacyType value) {
        addBtnWithIconAndTooltip(
                PrivacyTypeStyleProvider.getIcon(value),
                "btn btn-default",
                privacyTypeLang.getName(value),
                null, value, null, null);
    }

    @Inject
    En_CaseCommentPrivacyTypeLang privacyTypeLang;
}

