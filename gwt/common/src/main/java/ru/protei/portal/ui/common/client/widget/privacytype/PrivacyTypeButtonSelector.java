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
        if (extendedPrivacy) {
            addButton(En_CaseCommentPrivacyType.PRIVATE_CUSTOMERS);
        }
        addButton(En_CaseCommentPrivacyType.PRIVATE);
    }

    private void addButton(En_CaseCommentPrivacyType value) {
        addBtnWithIconAndTooltip(
                PrivacyTypeStyleProvider.getIcon(value),
                "btn btn-default",
                privacyTypeLang.getName(value),
                value);

        setEnsureDebugId(value, DebugIdsHelper.PRIVACY_TYPE.byId(value.getId()));
    }

    private boolean extendedPrivacy;

    @Inject
    En_CaseCommentPrivacyTypeLang privacyTypeLang;
}

