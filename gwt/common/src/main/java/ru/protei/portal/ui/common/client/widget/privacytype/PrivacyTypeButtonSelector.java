package ru.protei.portal.ui.common.client.widget.privacytype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.PrivacyTypeStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseCommentPrivacyTypeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

public class PrivacyTypeButtonSelector extends ToggleBtnGroup<En_CaseCommentPrivacyType> {
    @Inject
    public void init() {
        this.extendedPrivacy = false;
        fillOptions();
    }

    public void setExtendedPrivacy(boolean extendedPrivacy) {
        fillOptions(extendedPrivacy);
    }

    private void fillOptions(boolean extendedPrivacy) {
        if (this.extendedPrivacy == extendedPrivacy) {
            return;
        }
        this.extendedPrivacy = extendedPrivacy;
        fillOptions();
    }

    private void fillOptions() {
        clear();
        addButton(En_CaseCommentPrivacyType.PUBLIC);
        setEnsureDebugId(En_CaseCommentPrivacyType.PUBLIC, DebugIds.PRIVACY_TYPE_BUTTON.PUBLIC);
        if (extendedPrivacy) {
            addButton(En_CaseCommentPrivacyType.PRIVATE_CUSTOMERS);
            setEnsureDebugId(En_CaseCommentPrivacyType.PRIVATE_CUSTOMERS, DebugIds.PRIVACY_TYPE_BUTTON.PRIVATE_CUSTOMER);
        }
        addButton(En_CaseCommentPrivacyType.PRIVATE);
        setEnsureDebugId(En_CaseCommentPrivacyType.PRIVATE, DebugIds.PRIVACY_TYPE_BUTTON.PRIVATE);
    }

    private void addButton(En_CaseCommentPrivacyType value) {
        addBtnWithIconAndTooltip(
                PrivacyTypeStyleProvider.getIcon(value),
                "btn btn-default",
                privacyTypeLang.getName(value),
                value);
    }

    private boolean extendedPrivacy;

    @Inject
    En_CaseCommentPrivacyTypeLang privacyTypeLang;
}

