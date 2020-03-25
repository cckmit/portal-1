package ru.protei.portal.ui.common.client.widget.privacytype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.common.PrivacyTypeStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseCommentPrivacyTypeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

public class PrivacyTypeButtonSelector extends ToggleBtnGroup<En_CaseCommentPrivacyType> {
    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();
        for (En_CaseCommentPrivacyType value : En_CaseCommentPrivacyType.values()) {
            addBtnWithIconAndTooltip(
                    PrivacyTypeStyleProvider.getIcon(value),
                    "btn btn-default no-border",
                    privacyTypeLang.getName(value),
                    value);
            setEnsureDebugId(value, DebugIdsHelper.PRIVACY_TYPE.byOrdinal(value.ordinal()));
        }
    }
    @Inject
    En_CaseCommentPrivacyTypeLang privacyTypeLang;
}

